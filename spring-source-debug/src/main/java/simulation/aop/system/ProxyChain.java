package simulation.aop.system;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 代理调用信息  切点代理方法
 * @author huangfu
 */
public class ProxyChain {
	/**
	 * 切点的方法
	 */
	private Method method;
	/**
	 * 切点的对象
	 */
	private Object target;
	/**
	 * 切点的参数
	 */
	private Object[] args;

	public ProxyChain(Method method, Object target, Object... args) {
		this.method = method;
		this.target = target;
		this.args = args;
	}

	public Object invoker() throws InvocationTargetException, IllegalAccessException {
		method.setAccessible(true);
		return method.invoke(target, args);
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
}
