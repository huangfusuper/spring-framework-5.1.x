package expand.cglib.filter.callbacks;

import org.springframework.cglib.proxy.Callback;

import java.lang.reflect.Method;

public interface MethodConditionalCallback extends Callback {
	boolean isMatch(Method candidateMethod);
}
