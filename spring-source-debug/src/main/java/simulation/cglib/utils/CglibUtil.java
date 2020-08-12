package simulation.cglib.utils;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;
import simulation.cglib.callbacks.BeanMethodInterceptor;
import simulation.cglib.config.AppConfig;
import simulation.cglib.filter.MyMethodConditionFilter;
import simulation.cglib.pojo.BeanDeMap;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangfu
 */
public class CglibUtil {

	public static final Callback[] CALLBACKS = {new BeanMethodInterceptor(), NoOp.INSTANCE};
	private static ThreadLocal<Method> threadLocal = new ThreadLocal<>();
	private static Map<String, BeanDeMap> bdMap = new ConcurrentHashMap<>(8);
	private static Map<String,Object> singMap = new ConcurrentHashMap<>(8);


	public CglibUtil(Class classes) {
		enhanceConfig(classes);
		createBean();
	}

	private void createBean() {

	}

	private void enhanceConfig(Class classes){
		Enhancer enhancer = buildEnhancer(classes);
		String beanName = classes.getSimpleName();
		Class targetClass = enhancer.createClass();
		Enhancer.registerStaticCallbacks(targetClass,CALLBACKS);
		BeanDeMap beanDeMap = new BeanDeMap();
		beanDeMap.setaClass(targetClass);
		bdMap.put(beanName,beanDeMap);
	}

	private Enhancer buildEnhancer(Class classes){
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(classes);
		enhancer.setUseFactory(false);

		MyMethodConditionFilter filter = new MyMethodConditionFilter(CALLBACKS);
		enhancer.setCallbackFilter(filter);
		enhancer.setCallbackTypes(filter.getCallbackType());
		return enhancer;
	}
}
