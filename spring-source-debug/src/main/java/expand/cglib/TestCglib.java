package expand.cglib;

import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;

public class TestCglib {
	public static void main(String[] args) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(CglibService.class);
		enhancer.setCallbacks(new MethodInterceptor[]{new CglibInterceptor(),new CglibInterceptor2()});
		CglibService  o = (CglibService) enhancer.create();
		o.returnCglib();
	}
}

class CallbackFilterTest implements CallbackFilter{
	@Override
	public int accept(Method method) {
		return 0;
	}
}
