package expand.cglib.filter;

import expand.cglib.filter.callbacks.MethodConditionalCallback;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;

import java.lang.reflect.Method;

public class MyConditionalCallbackFilter implements CallbackFilter {
	private final Callback[] callbacks;
	private final Class<?>[] callbackTypes;

	public MyConditionalCallbackFilter(Callback[] callbacks) {
		this.callbacks = callbacks;
		this.callbackTypes = new Class<?>[callbacks.length];
		for (int i = 0; i < callbackTypes.length; i++) {
			this.callbackTypes[i] = callbacks[i].getClass();
		}
	}

	@Override
	public int accept(Method method) {
		for (int i = 0; i < this.callbacks.length; i++) {
			Callback callback = this.callbacks[i];
			if(!(callback instanceof MethodConditionalCallback)
					|| ((MethodConditionalCallback) callback).isMatch(method)){
				return i;
			}
		}
		throw new RuntimeException("----没有符合的回调方法------");
	}

	public Class<?>[] getCallbackTypes() {
		return callbackTypes;
	}
}
