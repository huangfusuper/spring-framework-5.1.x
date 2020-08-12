package simulation.cglib.filter;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import simulation.cglib.callbacks.ConditionMethodInterceptor;

import java.lang.reflect.Method;

/**
 * 方法拦截器
 * @author huangfu
 */
@SuppressWarnings("all")
public class MyMethodConditionFilter implements CallbackFilter {
	private final Callback[] callbacks;
	private final Class[] callbackType;

	public MyMethodConditionFilter(Callback[] callbacks) {
		this.callbacks = callbacks;
		callbackType = new Class[callbacks.length];
		for (int i = 0; i < this.callbacks.length; i++) {
			this.callbackType[i] = this.callbacks[i].getClass();
		}
	}

	@Override
	public int accept(Method method) {
		for (int i = 0; i < this.callbacks.length; i++) {
			Callback callback = callbacks[i];
			if(!(callback instanceof ConditionMethodInterceptor) || ((ConditionMethodInterceptor)callback).isMatch(method)){
				return i;
			}
		}
		throw new RuntimeException("-------------没有可用的回调方法--------------");
	}

	public Class[] getCallbackType() {
		return callbackType;
	}
}
