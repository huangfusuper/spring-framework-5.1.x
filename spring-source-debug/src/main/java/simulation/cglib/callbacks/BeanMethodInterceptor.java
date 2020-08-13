package simulation.cglib.callbacks;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import simulation.cglib.annotations.MyBean;
import simulation.cglib.utils.CglibUtil;

import java.lang.reflect.Method;

/**
 * 方法拦截器
 * @author huangfu
 */
public class BeanMethodInterceptor implements ConditionMethodInterceptor, MethodInterceptor {
	@Override
	public boolean isMatch(Method candidateMethod) {
		return (candidateMethod.isAnnotationPresent(MyBean.class) && candidateMethod.getDeclaringClass()!=Object.class);

	}

	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		if(CglibUtil.checkBeanFactory(method)){
			return methodProxy.invokeSuper(o, objects);
		}

		return cglibProxyLogic(method);
	}

	private Object cglibProxyLogic(Method method){
		String name = method.getName();
		return CglibUtil.getBean(name);
	}
}
