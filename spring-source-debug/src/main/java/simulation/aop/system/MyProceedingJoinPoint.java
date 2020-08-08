package simulation.aop.system;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangfu
 */
public class MyProceedingJoinPoint {
	/**
	 * 目标方法的参数
	 */
	private Object[] args;
	/**
	 * 目标对象
	 */
	private Object target;
	/**
	 * 目标方法
	 */
	private Method method;
	/**
	 * 存在的调用链
	 */
	private List<ProxyChain> proxyChains = new ArrayList<>(8);
	/**
	 * 当前调用链的指针位置
	 */
	private int chainsIndex = 0;


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

	public List<ProxyChain> getProxyChains() {
		return proxyChains;
	}

	public void setProxyChains(List<ProxyChain> proxyChains) {
		this.proxyChains = proxyChains;
	}

	public int getChainsIndex() {
		return chainsIndex;
	}

	public void setChainsIndex(int chainsIndex) {
		this.chainsIndex = chainsIndex;
	}
}
