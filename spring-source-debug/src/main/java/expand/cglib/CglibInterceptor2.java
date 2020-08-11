package expand.cglib;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibInterceptor2 implements MethodInterceptor {
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("222-----------------before");
		Object result = methodProxy.invokeSuper(o, objects);
		System.out.println("222-----------------after");
		return result;
	}
}
