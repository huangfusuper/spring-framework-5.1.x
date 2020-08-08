package simulation.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author huangfu
 */
public class MyProceedingJoinPoint {
	private Object[] args;
	private Object target;
	private Method method;


	public Object proceed(Object[] args) throws InvocationTargetException, IllegalAccessException {
		return method.invoke(target,args);
	}

	public Object proceed() throws InvocationTargetException, IllegalAccessException {
		return method.invoke(target);
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
}
