package simulation.cglib.utils;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.util.StringUtils;
import simulation.cglib.annotations.MyBean;
import simulation.cglib.callbacks.BeanMethodInterceptor;
import simulation.cglib.config.AppConfig;
import simulation.cglib.filter.MyMethodConditionFilter;
import simulation.cglib.pojo.BeanDeMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangfu
 */
@SuppressWarnings("all")
public class CglibUtil {

	/**
	 * 方法拦击回调参数
	 */
	private static final Callback[] CALLBACKS = {new BeanMethodInterceptor(), NoOp.INSTANCE};
	/**
	 * 方法本地线程变量
	 */
	public static ThreadLocal<Method> threadLocal = new ThreadLocal<>();

	/**
	 * bd Map
	 */
	private static Map<String, BeanDeMap> bdMap = new ConcurrentHashMap<>(8);

	/**
	 * 模拟单例池
	 */
	private static Map<String,Object> singMap = new ConcurrentHashMap<>(8);
	private final Class classes;

	/**
	 * 初始化环境
	 * @param classes
	 */
	public CglibUtil(Class classes) {
		this.classes = classes;
		//调用增强方法
		enhanceConfig();
		//创建bean
		createBean();
	}

	/**
	 * 创建bean
	 */
	private void createBean() {
		bdMap.forEach((key,beanDeMap) ->{
			//创建bean
			Object createBean = doCreateBean(beanDeMap);
			//放置到单例池
			singMap.put(key,createBean);
		});
	}

	/**
	 * 开始创建bean
	 * @param beanDeMap
	 * @return
	 */
	private static Object doCreateBean(BeanDeMap beanDeMap){
		try{
			//如果发现创建该bean需要 工厂方法  也就是@MyBean方法创建
			if(!StringUtils.isEmpty(beanDeMap.getBeanFactoryName())) {
				//获取对应的方法
				Method method = beanDeMap.getMethod();
				//向本地对象设置方法对象
				threadLocal.set(method);
				//反射执行增强类的方法
				Object invoke = method.invoke(singMap.get(beanDeMap.getBeanFactoryName()));
				//返回执行结果
				return invoke;
			}else{
				//普通bean直接创建
				Object resourceObject = beanDeMap.getaClass().newInstance();
				return resourceObject;
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 配置类增强
	 */
	private void enhanceConfig(){
		//创建一个cglib的增强器
		Enhancer enhancer = buildEnhancer();
		//获取beanName
		String beanName = classes.getSimpleName();
		//获取增强后的类的对象
		Class targetClass = enhancer.createClass();
		//注册回调方法  也就是拦截器的内部逻辑
		Enhancer.registerStaticCallbacks(targetClass,CALLBACKS);
		//创建一个BD
		BeanDeMap beanDeMap = new BeanDeMap();
		beanDeMap.setaClass(targetClass);
		//将类的信息放置到集合中
		bdMap.put(beanName,beanDeMap);
		//解析配置文件
		parseConfigClass(targetClass, beanName);
	}

	/**
	 * 解析配置类
	 * @param cglibProxyClass
	 * @param factoryName
	 */
	private void parseConfigClass(Class cglibProxyClass, String factoryName){
		//获取所有的方法
		Method[] declaredMethods = this.classes.getDeclaredMethods();
		Arrays.stream(declaredMethods).forEach(method -> {
			//获取需要加载的bean
			if(method.isAnnotationPresent(MyBean.class)){
				BeanDeMap beanDeMap = new BeanDeMap();
				String name = method.getName();
				if(bdMap.containsKey(name)) {
					throw new RuntimeException("beanName 重复");
				}
				//设置bd对的信息
				beanDeMap.setBeanFactoryName(factoryName);
				beanDeMap.setBeanFactoryClass(cglibProxyClass);
				beanDeMap.setMethod(method);
				bdMap.put(name,beanDeMap);
			}
		});
	}

	/**
	 * 构建增强器
	 * @return
	 */
	private Enhancer buildEnhancer(){
		//创建一个增强器
		Enhancer enhancer = new Enhancer();
		//设置要被代理的类
		enhancer.setSuperclass(classes);
		enhancer.setUseFactory(false);
		//构建和设置过滤器
		MyMethodConditionFilter filter = new MyMethodConditionFilter(CALLBACKS);
		enhancer.setCallbackFilter(filter);
		//设置回调类型
		enhancer.setCallbackTypes(filter.getCallbackType());
		return enhancer;
	}

	/**
	 * 验证工厂方法
	 * @param method
	 * @return
	 */
	public static boolean checkBeanFactory(Method method){
		return method.getName().equals(CglibUtil.threadLocal.get().getName());
	}

	public static Object getBean(String beanName){
		Object target = singMap.get(beanName);
		if(target == null){
			BeanDeMap beanDeMap = bdMap.get(beanName);
			target = doCreateBean(beanDeMap);
		}
		return target;
	}
}
