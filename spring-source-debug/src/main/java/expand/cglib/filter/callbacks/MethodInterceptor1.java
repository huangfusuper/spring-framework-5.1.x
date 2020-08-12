package expand.cglib.filter.callbacks;

import expand.cglib.annotations.MyBean;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MethodInterceptor1 implements MethodInterceptor, MethodConditionalCallback{
	@Override
	public boolean isMatch(Method candidateMethod) {
		return (candidateMethod.isAnnotationPresent(MyBean.class)) && candidateMethod.getDeclaringClass() != Object.class;
	}

	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("我被代理了");
		Object o1 = methodProxy.invokeSuper(o, objects);
		o1 += "代理逻辑嗷嗷叫";
		return o1;
	}
}
