package simulation.aop.system;

import simulation.aop.my.MyAspect;
import simulation.aop.my.TestServiceImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模拟AOP实现
 *
 * @author huangfu
 */
@SuppressWarnings("all")
public class BeanUtil {
	/**
	 * 假设这是单例池   模仿Spring,存放已经弄好的对象
	 */
	public final Map<String, Object> beanCache = new ConcurrentHashMap<>(8);

	/**
	 * 假设这是bd map 存放bean的信息 这里相对Spring进行简化了，只存储类对象足够我们使用了
	 */
	public final Map<String, Class> beanClassCache = new ConcurrentHashMap<String, Class>(8);
	/**
	 * 假设这是bean容器里面存储切面的缓存  存储的是切面的缓存
	 */
	public final List<Class> aspectjClassCache = new ArrayList<>(8);

	/**
	 * 拦截规则  存储注解对应的规则链
	 */
	public final Map<String, List<ProxyChain>> proxyRule = new ConcurrentHashMap<>();

	/**
	 * 我们假设Spring完成了基础扫描步骤，已经将bd存放再了容器里面
	 * 假设假设哈
	 */
	public BeanUtil() throws InstantiationException, IllegalAccessException {
		beanClassCache.put("testService", TestServiceImpl.class);
		aspectjClassCache.add(MyAspect.class);
		//解析切面
		parseAspectjClass();
	}


	/**
	 * 初始化bean
	 */
	public void initBean() {
		beanClassCache.forEach((key, value) -> {
			//判断是否有需要代理的方法
			try {
				//创建所有的类 转换为bean
				createBean(key, value, hasProxy(value));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}

		});
	}

	/**
	 * 创建bean
	 *
	 * @param beanName bean的名称
	 * @param classes  bean的类对象
	 * @param isProxy  是否需要代理
	 */
	public void createBean(String beanName, Class classes, boolean isProxy) throws IllegalAccessException, InstantiationException {
		Class<?>[] interfaces = getInterfaces(classes);
		Object target = classes.newInstance();
		//需要被代理 这里没有模仿cglib而是使用的jdk动态代理 所以必须需要接口 Spring是两种接口混合使用的
		if (isProxy && interfaces != null && interfaces.length > 0) {
			//创建jdk动态代理的对象
			MyJdkDynamicAopProxy myJdkDynamicAopProxy = new MyJdkDynamicAopProxy(this, target);
			//返回代理对象
			target = Proxy.newProxyInstance(BeanUtil.class.getClassLoader(), interfaces, myJdkDynamicAopProxy);
			//存储再缓存池
			beanCache.put(beanName, target);
		} else {
			//不需要代理也存储再缓存池
			beanCache.put(beanName, target);
		}
	}

	/**
	 * 获取bean对象
	 *
	 * @param beanName beanName
	 * @param <T>
	 * @return
	 */
	public <T> T getBean(String beanName) {
		return (T) beanCache.get(beanName);
	}


	/**
	 * 获取对应类的接口
	 *
	 * @param classes 要获取的类对象
	 * @return 该类对象的接口
	 */
	private Class<?>[] getInterfaces(Class classes) {
		return classes.getInterfaces();
	}

	/**
	 * 内部是否存在需要被拦截的方法对象
	 *
	 * @param targetClass 目标类对象
	 * @return 返回是否需要代理
	 */
	private boolean hasProxy(Class targetClass) {
		//获取所有的方法
		Method[] declaredMethods = targetClass.getDeclaredMethods();
		for (Method method : Arrays.asList(declaredMethods)) {
			//获取方法上的注解
			Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
			//当方法存在注释的时候 开始判断是否方法需要被代理
			if (declaredAnnotations != null && declaredAnnotations.length > 0) {
				for (Annotation annotation : Arrays.asList(declaredAnnotations)) {
					//如果解析规则存在这个注解就返回true
					if (proxyRule.containsKey(annotation.annotationType().getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 解析切面类
	 */
	private void parseAspectjClass() throws IllegalAccessException, InstantiationException {
		for (Class aClass : aspectjClassCache) {
			//获取切面的所有方法
			Method[] declaredMethods = aClass.getDeclaredMethods();
			for (Method method : Arrays.asList(declaredMethods)) {
				//寻找携带MyAround注解的切面方法
				MyAround myAroundAnntation = method.getAnnotation(MyAround.class);
				if (myAroundAnntation != null) {
					//拿到切点标志 也就是未来切点的方法
					Class<? extends Annotation> targetClassAnnotation = myAroundAnntation.targetClass();
					//实例化切面
					Object proxyTarget = aClass.newInstance();
					//创建调用链实体
					ProxyChain proxyChain = new ProxyChain(method, proxyTarget);
					//构建对应规则的调用链
					String classAnnotationName = targetClassAnnotation.getName();
					if (proxyRule.containsKey(classAnnotationName)) {
						proxyRule.get(classAnnotationName).add(proxyChain);
					} else {
						List<ProxyChain> proxyChains = new ArrayList<>();
						proxyChains.add(proxyChain);
						proxyRule.put(classAnnotationName, proxyChains);
					}
				}
			}
		}
	}


}
