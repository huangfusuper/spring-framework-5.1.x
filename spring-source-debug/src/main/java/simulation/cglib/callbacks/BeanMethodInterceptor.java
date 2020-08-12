package simulation.cglib.callbacks;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import simulation.cglib.annotations.MyBean;

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
		System.out.println("-----方法被代理------");
		Object result = methodProxy.invokeSuper(o, objects);
		return result;
	}
}
