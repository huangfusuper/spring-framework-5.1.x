/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.asm.Type;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.SimpleInstantiationStrategy;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.DefaultGeneratorStrategy;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.cglib.transform.ClassEmitterTransformer;
import org.springframework.cglib.transform.TransformingClassGenerator;
import org.springframework.lang.Nullable;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.SpringObjenesis;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/**
 * 通过生成一个CGLIB子类来增强{@link Configuration}类，该子类与Spring容器进行交互以遵循
 * {@code @Bean}方法的bean作用域语义。每个此类{@code @Bean}方法将在生成的子类中被覆盖，
 * 如果容器实际请求构造新实例，则仅委托给实际的{@code @Bean}方法实现。
 * 否则，对此类{@code @Bean}方法的调用用作对容器的*引用，并按名称获取相应的bean。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see #enhance
 * @see ConfigurationClassPostProcessor
 */
class ConfigurationClassEnhancer {

	// 要使用的回调。请注意，这些回调必须是无状态的。
	private static final Callback[] CALLBACKS = new Callback[] {
			//这个是真正能够Bean方法多次调用返回的是一个bean实例的实际拦截方法
			new BeanMethodInterceptor(),
			//拦截 BeanFactoryAware 为里面的 setBeanFactory 赋值
			new BeanFactoryAwareMethodInterceptor(),
			NoOp.INSTANCE
	};

	private static final ConditionalCallbackFilter CALLBACK_FILTER = new ConditionalCallbackFilter(CALLBACKS);

	private static final String BEAN_FACTORY_FIELD = "$$beanFactory";


	private static final Log logger = LogFactory.getLog(ConfigurationClassEnhancer.class);

	private static final SpringObjenesis objenesis = new SpringObjenesis();


	/**
	 * 加载指定的类并生成配备了该类的CGLIB子类 能够感知作用域和其他bean语义的容器感知回调。
	 * @return the enhanced subclass
	 */
	public Class<?> enhance(Class<?> configClass, @Nullable ClassLoader classLoader) {
		// 如果已经实现了该接口，证明已经被代理过了，直接返回
		if (EnhancedConfiguration.class.isAssignableFrom(configClass)) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Ignoring request to enhance %s as it has " +
						"already been enhanced. This usually indicates that more than one " +
						"ConfigurationClassPostProcessor has been registered (e.g. via " +
						"<context:annotation-config>). This is harmless, but you may " +
						"want check your configuration and remove one CCPP if possible",
						configClass.getName()));
			}
			return configClass;
		}
		//没被代理过。就先调用newEnhancer()方法创建一个增强器Enhancer
		//然后在使用这个增强器，生成代理类字节码Class对象
		//创建一个新的CGLIB Enhancer实例，并且做好相应配置
		Class<?> enhancedClass = createClass(newEnhancer(configClass, classLoader));
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("Successfully enhanced %s; enhanced class name is: %s",
					configClass.getName(), enhancedClass.getName()));
		}
		return enhancedClass;
	}

	/**
	 * Creates a new CGLIB {@link Enhancer} instance.
	 */
	private Enhancer newEnhancer(Class<?> configSuperClass, @Nullable ClassLoader classLoader) {
		Enhancer enhancer = new Enhancer();
		// 目标类型：会以这个作为父类型来生成字节码子类
		enhancer.setSuperclass(configSuperClass);
		//代理类实现EnhancedConfiguration接口，这个接口继承了BeanFactoryAware接口
		enhancer.setInterfaces(new Class<?>[] {EnhancedConfiguration.class});
		// 设置生成的代理类不实现org.springframework.cglib.proxy.Factory接口
		enhancer.setUseFactory(false);
		//设置代理类名称的生成策略：Spring定义的一个生成策略 你名称中会有“BySpringCGLIB”字样
		enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
		enhancer.setStrategy(new BeanFactoryAwareGeneratorStrategy(classLoader));
		//设置拦截器/过滤器
		enhancer.setCallbackFilter(CALLBACK_FILTER);
		enhancer.setCallbackTypes(CALLBACK_FILTER.getCallbackTypes());
		return enhancer;
	}

	/**
	 * 使用增强器生成超类的子类， 确保为新的子类注册了回调。
	 */
	private Class<?> createClass(Enhancer enhancer) {
		Class<?> subclass = enhancer.createClass();
		// 静态注册回调（与线程本地相反） 对于在OSGi环境（SPR-5932）中使用至关重要。
		Enhancer.registerStaticCallbacks(subclass, CALLBACKS);
		return subclass;
	}


	/**
	 * Marker interface to be implemented by all @Configuration CGLIB subclasses.
	 * Facilitates idempotent behavior for {@link ConfigurationClassEnhancer#enhance}
	 * through checking to see if candidate classes are already assignable to it, e.g.
	 * have already been enhanced.
	 * <p>Also extends {@link BeanFactoryAware}, as all enhanced {@code @Configuration}
	 * classes require access to the {@link BeanFactory} that created them.
	 * <p>Note that this interface is intended for framework-internal use only, however
	 * must remain public in order to allow access to subclasses generated from other
	 * packages (i.e. user code).
	 */
	public interface EnhancedConfiguration extends BeanFactoryAware {
	}


	/**
	 * Conditional {@link Callback}.
	 * @see ConditionalCallbackFilter
	 */
	private interface ConditionalCallback extends Callback {

		boolean isMatch(Method candidateMethod);
	}


	/**
	 * A {@link CallbackFilter} that works by interrogating {@link Callback Callbacks} in the order
	 * that they are defined via {@link ConditionalCallback}.
	 */
	private static class ConditionalCallbackFilter implements CallbackFilter {

		private final Callback[] callbacks;

		private final Class<?>[] callbackTypes;

		public ConditionalCallbackFilter(Callback[] callbacks) {
			this.callbacks = callbacks;
			this.callbackTypes = new Class<?>[callbacks.length];
			for (int i = 0; i < callbacks.length; i++) {
				this.callbackTypes[i] = callbacks[i].getClass();
			}
		}

		@Override
		public int accept(Method method) {
			for (int i = 0; i < this.callbacks.length; i++) {
				Callback callback = this.callbacks[i];
				if (!(callback instanceof ConditionalCallback) || ((ConditionalCallback) callback).isMatch(method)) {
					return i;
				}
			}
			throw new IllegalStateException("No callback available for method " + method.getName());
		}

		public Class<?>[] getCallbackTypes() {
			return this.callbackTypes;
		}
	}


	/**
	 * Custom extension of CGLIB's DefaultGeneratorStrategy, introducing a {@link BeanFactory} field.
	 * Also exposes the application ClassLoader as thread context ClassLoader for the time of
	 * class generation (in order for ASM to pick it up when doing common superclass resolution).
	 */
	private static class BeanFactoryAwareGeneratorStrategy extends DefaultGeneratorStrategy {

		@Nullable
		private final ClassLoader classLoader;

		public BeanFactoryAwareGeneratorStrategy(@Nullable ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		@Override
		protected ClassGenerator transform(ClassGenerator cg) throws Exception {
			ClassEmitterTransformer transformer = new ClassEmitterTransformer() {
				@Override
				public void end_class() {
					declare_field(Constants.ACC_PUBLIC, BEAN_FACTORY_FIELD, Type.getType(BeanFactory.class), null);
					super.end_class();
				}
			};
			return new TransformingClassGenerator(cg, transformer);
		}

		@Override
		public byte[] generate(ClassGenerator cg) throws Exception {
			if (this.classLoader == null) {
				return super.generate(cg);
			}

			Thread currentThread = Thread.currentThread();
			ClassLoader threadContextClassLoader;
			try {
				threadContextClassLoader = currentThread.getContextClassLoader();
			}
			catch (Throwable ex) {
				// Cannot access thread context ClassLoader - falling back...
				return super.generate(cg);
			}

			boolean overrideClassLoader = !this.classLoader.equals(threadContextClassLoader);
			if (overrideClassLoader) {
				currentThread.setContextClassLoader(this.classLoader);
			}
			try {
				return super.generate(cg);
			}
			finally {
				if (overrideClassLoader) {
					// Reset original thread context ClassLoader.
					currentThread.setContextClassLoader(threadContextClassLoader);
				}
			}
		}
	}


	/**
	 * 拦截对任何{@link BeanFactoryAware＃setBeanFactory（BeanFactory）}的调用 {@code @Configuration}类实例，用于记录{@link BeanFactory}。
	 * @see EnhancedConfiguration
	 */
	private static class BeanFactoryAwareMethodInterceptor implements MethodInterceptor, ConditionalCallback {

		@Override
		@Nullable
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			//找到本类（代理类）里名为`$$beanFactory`的字段
			Field field = ReflectionUtils.findField(obj.getClass(), BEAN_FACTORY_FIELD);
			//若没找到直接报错。若找到了此字段，就给此字段赋值
			Assert.state(field != null, "Unable to find generated BeanFactory field");
			field.set(obj, args[0]);

			// 实际的（非CGLIB）超类是否实现BeanFactoryAware？
			// 如果是这样，请调用其setBeanFactory（）方法。如果没有，请退出。
			//如果用户类（也就是你自己定义的类）自己实现了该接口，那么别担心，也会给你赋值上
			if (BeanFactoryAware.class.isAssignableFrom(ClassUtils.getUserClass(obj.getClass().getSuperclass()))) {
				return proxy.invokeSuper(obj, args);
			}
			return null;
		}

		/**
		 * 执行到setBeanFactory(xxx)方法时匹配成功
		 * @param candidateMethod
		 * @return
		 */
		@Override
		public boolean isMatch(Method candidateMethod) {
			return isSetBeanFactory(candidateMethod);
		}

		/**
		 * 此方法标记为public static 是因为下面这个拦截器也会用到
		 * 执行到setBeanFactory(xxx)方法时匹配成功
		 * 代理类实现了EnhancedConfiguration接口（间接实现了BeanFactoryAware接口），所以该拦截器的执行时机为：在Spring初始化代理类实例时执行拦截。
		 * @param candidateMethod
		 * @return
		 */
		public static boolean isSetBeanFactory(Method candidateMethod) {
			return (candidateMethod.getName().equals("setBeanFactory") &&
					candidateMethod.getParameterCount() == 1 &&
					BeanFactory.class == candidateMethod.getParameterTypes()[0] &&
					BeanFactoryAware.class.isAssignableFrom(candidateMethod.getDeclaringClass()));
		}
	}


	/**
	 * 拦截任何带有{@link Bean}注释的方法的调用，以确保正确 Bean语义处理，例如作用域和AOP代理。
	 * @see Bean
	 * @see ConfigurationClassEnhancer
	 */
	private static class BeanMethodInterceptor implements MethodInterceptor, ConditionalCallback {

		/**
		 * 增强{@link Bean @Bean}方法以检查提供的BeanFactory中的 这个bean对象的存在。
		 * @throws Throwable 作为所有在调用时可能引发的异常的统筹 代理方法的超级实现，即实际的{@code @Bean}方法
		 * 当该方法经过匹配成功后 会进入到这个拦截方法  这个是解决bean方法只被创建一次的重要逻辑
		 */
		@Override
		@Nullable
		public Object intercept(Object enhancedConfigInstance, Method beanMethod, Object[] beanMethodArgs,
					MethodProxy cglibMethodProxy) throws Throwable {
			//通过反射，获取到Bean工厂。也就是 $$beanFactory 这个属性的值
			//也就是上一个拦截器被注入的值
			ConfigurableBeanFactory beanFactory = getBeanFactory(enhancedConfigInstance);
			//拿到Bean的名称
			String beanName = BeanAnnotationHelper.determineBeanNameFor(beanMethod);

			// 确定此bean是否为作用域代理
			//方法头上是否标注有@Scoped注解
			if (BeanAnnotationHelper.isScopedProxy(beanMethod)) {
				String scopedBeanName = ScopedProxyCreator.getTargetBeanName(beanName);
				if (beanFactory.isCurrentlyInCreation(scopedBeanName)) {
					beanName = scopedBeanName;
				}
			}

			// 要处理bean间方法引用的情况，我们必须显式检查 已缓存实例的容器。

			// 首先，检查所请求的bean是否为FactoryBean。如果是这样，请创建一个子类 拦截对getObject（）的调用并返回所有缓存的bean实例的代理。
			// 这样可以确保从@Bean方法中调用FactoryBean的语义
			// 与在XML中引用FactoryBean相同。请参阅SPR-6602。
			//首先：检查所请求的Bean是否是FactoryBean。
			//如果是的话，就创建一个代理子类，拦截它的getObject()方法以返回容器里的实例
			// 这样做保证了方法返回一个FactoryBean和@Bean的语义是效果一样的，确保了不会重复创建多个Bean
			if (factoryContainsBean(beanFactory, BeanFactory.FACTORY_BEAN_PREFIX + beanName) &&
					factoryContainsBean(beanFactory, beanName)) {
				//先得到这个工厂Bean
				Object factoryBean = beanFactory.getBean(BeanFactory.FACTORY_BEAN_PREFIX + beanName);
				if (factoryBean instanceof ScopedProxyFactoryBean) {
					// Scoped proxy factory beans are a special case and should not be further proxied
					// 如果工厂Bean已经是一个Scope代理Bean，则不需要再增强
					// 因为它已经能够满足FactoryBean延迟初始化Bean了~
				}
				else {
					// 它是候选FactoryBean-继续进行增强
					return enhanceFactoryBean(factoryBean, beanMethod.getReturnType(), beanFactory, beanName);
				}
			}
			// 检查给定的方法是否与当前调用的容器相对应工厂方法。
			// 比较方法名称和参数列表来确定是否是同一个方法
			// 怎么理解这句话，参照下面详解吧
			if (isCurrentlyInvokedFactoryMethod(beanMethod)) {
				// 这是个小细节：若你@Bean返回的是BeanFactoryPostProcessor类型
				// 请你使用static静态方法，否则会打印这句日志的~~~~
				// 因为如果是非静态方法，部分后置处理失效处理不到你，可能对你程序有影像
				// 当然也可能没影响，所以官方也只是建议而已~~~
				if (logger.isInfoEnabled() &&
						BeanFactoryPostProcessor.class.isAssignableFrom(beanMethod.getReturnType())) {
					logger.info(String.format("@Bean method %s.%s is non-static and returns an object " +
									"assignable to Spring's BeanFactoryPostProcessor interface. This will " +
									"result in a failure to process annotations such as @Autowired, " +
									"@Resource and @PostConstruct within the method's declaring " +
									"@Configuration class. Add the 'static' modifier to this method to avoid " +
									"these container lifecycle issues; see @Bean javadoc for complete details.",
							beanMethod.getDeclaringClass().getSimpleName(), beanMethod.getName()));
				}
				// 这表示：当前方法，就是这个被拦截的方法，那就没啥好说的
				// 相当于在代理代理类里执行了super(xxx);
				// 但是，但是，但是，此时的this依旧是代理类
				//这个事实上上调用的是本身的方法  最终会再次被调用到下面的 resolveBeanReference 方法
				return cglibMethodProxy.invokeSuper(enhancedConfigInstance, beanMethodArgs);
			}
			//方法里调用的实例化方法会交给这里来执行
			return resolveBeanReference(beanMethod, beanMethodArgs, beanFactory, beanName);
		}

		private Object resolveBeanReference(Method beanMethod, Object[] beanMethodArgs,
				ConfigurableBeanFactory beanFactory, String beanName) {

			// 用户（即不是工厂）正在通过调用来请求该bean
			// bean方法，直接或间接。该bean可能已经被标记
			// 在某些自动装配场景中为“在创建中”；如果是这样，请暂时设置
			// 为了避免异常，将递增状态设置为false。
			//当前bean（son这个Bean）是否正在创建中... 本处为false嘛
			boolean alreadyInCreation = beanFactory.isCurrentlyInCreation(beanName);
			try {
				// 如果该Bean确实正在创建中，先把它标记下，放置后面getBean报错
				if (alreadyInCreation) {
					beanFactory.setCurrentlyInCreation(beanName, false);
				}
				//更具该方法的入参，决定后面使用getBean(beanName)还是getBean(beanName,args)
				//基本原则是：但凡只要有一个入参为null，就调用getBean(beanName)
				boolean useArgs = !ObjectUtils.isEmpty(beanMethodArgs);
				if (useArgs && beanFactory.isSingleton(beanName)) {
					// 引用null参数只是为了参考，
					// 希望他们能自动接线以获取常规的单例参考？
					// 一个安全的假设，因为@Bean单例参数不能是可选的...
					for (Object arg : beanMethodArgs) {
						if (arg == null) {
							useArgs = false;
							break;
						}
					}
				}
				//通过getBean从容器中拿到这个实例
				Object beanInstance = (useArgs ? beanFactory.getBean(beanName, beanMethodArgs) :
						beanFactory.getBean(beanName));

				//方法返回类型和Bean实际类型做个比较，因为有可能类型不一样
				// 什么时候会出现类型不一样呢？当BeanDefinition定义信息类型被覆盖的时候，就可能出现此现象
				if (!ClassUtils.isAssignableValue(beanMethod.getReturnType(), beanInstance)) {
					// 通过equals（null）检查检测受包保护的NullBean实例
					if (beanInstance.equals(null)) {
						if (logger.isDebugEnabled()) {
							logger.debug(String.format("@Bean method %s.%s called as bean reference " +
									"for type [%s] returned null bean; resolving to null value.",
									beanMethod.getDeclaringClass().getSimpleName(), beanMethod.getName(),
									beanMethod.getReturnType().getName()));
						}
						beanInstance = null;
					}
					else {
						String msg = String.format("@Bean method %s.%s called as bean reference " +
								"for type [%s] but overridden by non-compatible bean instance of type [%s].",
								beanMethod.getDeclaringClass().getSimpleName(), beanMethod.getName(),
								beanMethod.getReturnType().getName(), beanInstance.getClass().getName());
						try {
							BeanDefinition beanDefinition = beanFactory.getMergedBeanDefinition(beanName);
							msg += " Overriding bean of same name declared in: " + beanDefinition.getResourceDescription();
						}
						catch (NoSuchBeanDefinitionException ex) {
							// Ignore - simply no detailed message then.
						}
						throw new IllegalStateException(msg);
					}
				}
				Method currentlyInvoked = SimpleInstantiationStrategy.getCurrentlyInvokedFactoryMethod();
				if (currentlyInvoked != null) {
					String outerBeanName = BeanAnnotationHelper.determineBeanNameFor(currentlyInvoked);
					beanFactory.registerDependentBean(beanName, outerBeanName);
				}
				return beanInstance;
			}
			finally {
				if (alreadyInCreation) {
					beanFactory.setCurrentlyInCreation(beanName, true);
				}
			}
		}

		/**
		 * 匹配拦截条件
		 * @param candidateMethod 所有的method回调
		 * 判断逻辑如下：
		 *       该方法不能是Object的方法（即使你Object的方法标注了@Bean，我也不认）
		 *       不能是setBeanFactory()方法。这很容易理解，它交给上个拦截器搞定即可
		 *       方法必须标注标注有@Bean注解
		 * 方法只要是个Method即可，无论是static方法还是普通方法，都会参与此判断逻辑哦
		 * @return 改方法是否被拦截
		 */
		@Override
		public boolean isMatch(Method candidateMethod) {
			return (candidateMethod.getDeclaringClass() != Object.class &&
					!BeanFactoryAwareMethodInterceptor.isSetBeanFactory(candidateMethod) &&
					BeanAnnotationHelper.isBeanAnnotated(candidateMethod));
		}

		private ConfigurableBeanFactory getBeanFactory(Object enhancedConfigInstance) {
			Field field = ReflectionUtils.findField(enhancedConfigInstance.getClass(), BEAN_FACTORY_FIELD);
			Assert.state(field != null, "Unable to find generated bean factory field");
			Object beanFactory = ReflectionUtils.getField(field, enhancedConfigInstance);
			Assert.state(beanFactory != null, "BeanFactory has not been injected into @Configuration class");
			Assert.state(beanFactory instanceof ConfigurableBeanFactory,
					"Injected BeanFactory is not a ConfigurableBeanFactory");
			return (ConfigurableBeanFactory) beanFactory;
		}

		/**
		 * Check the BeanFactory to see whether the bean named <var>beanName</var> already
		 * exists. Accounts for the fact that the requested bean may be "in creation", i.e.:
		 * we're in the middle of servicing the initial request for this bean. From an enhanced
		 * factory method's perspective, this means that the bean does not actually yet exist,
		 * and that it is now our job to create it for the first time by executing the logic
		 * in the corresponding factory method.
		 * <p>Said another way, this check repurposes
		 * {@link ConfigurableBeanFactory#isCurrentlyInCreation(String)} to determine whether
		 * the container is calling this method or the user is calling this method.
		 * @param beanName name of bean to check for
		 * @return whether <var>beanName</var> already exists in the factory
		 */
		private boolean factoryContainsBean(ConfigurableBeanFactory beanFactory, String beanName) {
			return (beanFactory.containsBean(beanName) && !beanFactory.isCurrentlyInCreation(beanName));
		}

		/**
		 * Check whether the given method corresponds to the container's currently invoked
		 * factory method. Compares method name and parameter types only in order to work
		 * around a potential problem with covariant return types (currently only known
		 * to happen on Groovy classes).
		 */
		private boolean isCurrentlyInvokedFactoryMethod(Method method) {
			Method currentlyInvoked = SimpleInstantiationStrategy.getCurrentlyInvokedFactoryMethod();
			return (currentlyInvoked != null && method.getName().equals(currentlyInvoked.getName()) &&
					Arrays.equals(method.getParameterTypes(), currentlyInvoked.getParameterTypes()));
		}

		/**
		 * Create a subclass proxy that intercepts calls to getObject(), delegating to the current BeanFactory
		 * instead of creating a new instance. These proxies are created only when calling a FactoryBean from
		 * within a Bean method, allowing for proper scoping semantics even when working against the FactoryBean
		 * instance directly. If a FactoryBean instance is fetched through the container via &-dereferencing,
		 * it will not be proxied. This too is aligned with the way XML configuration works.
		 */
		private Object enhanceFactoryBean(final Object factoryBean, Class<?> exposedType,
				final ConfigurableBeanFactory beanFactory, final String beanName) {

			try {
				Class<?> clazz = factoryBean.getClass();
				boolean finalClass = Modifier.isFinal(clazz.getModifiers());
				boolean finalMethod = Modifier.isFinal(clazz.getMethod("getObject").getModifiers());
				if (finalClass || finalMethod) {
					if (exposedType.isInterface()) {
						if (logger.isTraceEnabled()) {
							logger.trace("Creating interface proxy for FactoryBean '" + beanName + "' of type [" +
									clazz.getName() + "] for use within another @Bean method because its " +
									(finalClass ? "implementation class" : "getObject() method") +
									" is final: Otherwise a getObject() call would not be routed to the factory.");
						}
						return createInterfaceProxyForFactoryBean(factoryBean, exposedType, beanFactory, beanName);
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("Unable to proxy FactoryBean '" + beanName + "' of type [" +
									clazz.getName() + "] for use within another @Bean method because its " +
									(finalClass ? "implementation class" : "getObject() method") +
									" is final: A getObject() call will NOT be routed to the factory. " +
									"Consider declaring the return type as a FactoryBean interface.");
						}
						return factoryBean;
					}
				}
			}
			catch (NoSuchMethodException ex) {
				// No getObject() method -> shouldn't happen, but as long as nobody is trying to call it...
			}

			return createCglibProxyForFactoryBean(factoryBean, beanFactory, beanName);
		}

		private Object createInterfaceProxyForFactoryBean(final Object factoryBean, Class<?> interfaceType,
				final ConfigurableBeanFactory beanFactory, final String beanName) {

			return Proxy.newProxyInstance(
					factoryBean.getClass().getClassLoader(), new Class<?>[] {interfaceType},
					(proxy, method, args) -> {
						if (method.getName().equals("getObject") && args == null) {
							return beanFactory.getBean(beanName);
						}
						return ReflectionUtils.invokeMethod(method, factoryBean, args);
					});
		}

		private Object createCglibProxyForFactoryBean(final Object factoryBean,
				final ConfigurableBeanFactory beanFactory, final String beanName) {

			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(factoryBean.getClass());
			enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
			enhancer.setCallbackType(MethodInterceptor.class);

			// Ideally create enhanced FactoryBean proxy without constructor side effects,
			// analogous to AOP proxy creation in ObjenesisCglibAopProxy...
			Class<?> fbClass = enhancer.createClass();
			Object fbProxy = null;

			if (objenesis.isWorthTrying()) {
				try {
					fbProxy = objenesis.newInstance(fbClass, enhancer.getUseCache());
				}
				catch (ObjenesisException ex) {
					logger.debug("Unable to instantiate enhanced FactoryBean using Objenesis, " +
							"falling back to regular construction", ex);
				}
			}

			if (fbProxy == null) {
				try {
					fbProxy = ReflectionUtils.accessibleConstructor(fbClass).newInstance();
				}
				catch (Throwable ex) {
					throw new IllegalStateException("Unable to instantiate enhanced FactoryBean using Objenesis, " +
							"and regular FactoryBean instantiation via default constructor fails as well", ex);
				}
			}

			((Factory) fbProxy).setCallback(0, (MethodInterceptor) (obj, method, args, proxy) -> {
				if (method.getName().equals("getObject") && args.length == 0) {
					return beanFactory.getBean(beanName);
				}
				return proxy.invoke(factoryBean, args);
			});

			return fbProxy;
		}
	}

}
