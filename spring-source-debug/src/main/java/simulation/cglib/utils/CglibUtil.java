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
		enhanceConfig();
		createBean();
	}

	/**
	 * 创建bean
	 */
	private void createBean() {
		bdMap.forEach((key,beanDeMap) ->{
			Object createBean = doCreateBean(beanDeMap);
			singMap.put(key,createBean);
		});
	}

	private static Object doCreateBean(BeanDeMap beanDeMap){
		try{
			if(!StringUtils.isEmpty(beanDeMap.getBeanFactoryName())) {
				Method method = beanDeMap.getMethod();
				threadLocal.set(method);
				Object invoke = method.invoke(singMap.get(beanDeMap.getBeanFactoryName()));
				return invoke;
			}else{
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
		Enhancer enhancer = buildEnhancer();
		String beanName = classes.getSimpleName();
		Class targetClass = enhancer.createClass();
		Enhancer.registerStaticCallbacks(targetClass,CALLBACKS);
		BeanDeMap beanDeMap = new BeanDeMap();
		beanDeMap.setaClass(targetClass);
		bdMap.put(beanName,beanDeMap);
		parseConfigClass(targetClass, beanName);
	}

	/**
	 * 解析配置类
	 * @param cglibProxyClass
	 * @param factoryName
	 */
	private void parseConfigClass(Class cglibProxyClass, String factoryName){
		Method[] declaredMethods = this.classes.getDeclaredMethods();
		Arrays.stream(declaredMethods).forEach(method -> {
			if(method.isAnnotationPresent(MyBean.class)){
				BeanDeMap beanDeMap = new BeanDeMap();
				String name = method.getName();
				if(bdMap.containsKey(name)) {
					throw new RuntimeException("beanName 重复");
				}
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
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(classes);
		enhancer.setUseFactory(false);

		MyMethodConditionFilter filter = new MyMethodConditionFilter(CALLBACKS);
		enhancer.setCallbackFilter(filter);
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
