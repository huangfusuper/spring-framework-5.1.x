package simulation.cglib.callbacks;

import org.springframework.cglib.proxy.Callback;

import java.lang.reflect.Method;

/**
 * 方法拦截器条件
 * @author huangfu
 */
public interface ConditionMethodInterceptor extends Callback {

	/**
	 * 这个方法是否匹配
	 * @param candidateMethod
	 * @return
	 */
	boolean isMatch(Method candidateMethod);
}
