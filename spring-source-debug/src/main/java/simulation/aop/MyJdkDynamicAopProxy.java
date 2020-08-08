package simulation.aop;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理执行器
 * @author huangfu
 */
public class MyJdkDynamicAopProxy implements InvocationHandler, Serializable {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}
}
