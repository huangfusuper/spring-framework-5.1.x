package expand.cglib;

import expand.cglib.filter.MyConditionalCallbackFilter;
import expand.cglib.filter.callbacks.MethodInterceptor1;
import expand.cglib.service.UserService;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;

public class CglibMainTest {

	public static void main(String[] args) {
		Enhancer enhancer = new Enhancer();
		Callback[] callbacks = {new MethodInterceptor1(), NoOp.INSTANCE};
		MyConditionalCallbackFilter filter = new MyConditionalCallbackFilter(callbacks);

		// 目标类型：会以这个作为父类型来生成字节码子类
		enhancer.setSuperclass(UserService.class);
		//设置代理类名称的生成策略：Spring定义的一个生成策略 你名称中会有“BySpringCGLIB”字样
		enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
		//设置拦截器/过滤器
		enhancer.setCallbackFilter(filter);
		enhancer.setCallbackTypes(filter.getCallbackTypes());
		enhancer.setUseFactory(false);
		enhancer.setCallbacks(callbacks);
		UserService o = (UserService) enhancer.create();
		System.out.println(o);
		System.out.println(o.print());
	}
}
