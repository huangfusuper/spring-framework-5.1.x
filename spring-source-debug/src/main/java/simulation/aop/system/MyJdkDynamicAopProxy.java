package simulation.aop.system;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 动态代理执行器
 *
 * @author huangfu
 */
public class MyJdkDynamicAopProxy implements InvocationHandler, Serializable {
	int chainsIndex = 0;
	Object target;

	private final BeanUtil beanUtil;

	public MyJdkDynamicAopProxy(BeanUtil beanUtil, Object target) {
		this.beanUtil = beanUtil;
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
		Annotation[] targetAnnotations = targetMethod.getDeclaredAnnotations();
		//没注解  不代理
		if (targetAnnotations.length <= 0) {
			return method.invoke(target, args);
		}
		List<ProxyChain> proxyChains = new ArrayList<>(8);
		//获取这个方法的所有的注解，获取所以配置注解的切面方法
		Arrays.stream(targetAnnotations).forEach(annotation -> {
			Class<? extends Annotation> annotationClass = annotation.annotationType();
			if (beanUtil.proxyRule.containsKey(annotationClass.getName())) {
				proxyChains.addAll(beanUtil.proxyRule.get(annotationClass.getName()));
			}
		});
		//若拦截规则为空就直接执行就行了
		if (CollectionUtils.isEmpty(proxyChains)) {
			return method.invoke(target, args);
		}

		if (chainsIndex == proxyChains.size()) {
			return method.invoke(target, args);
		}
		//构建参数
		MyProceedingJoinPoint myProceedingJoinPoint = new MyProceedingJoinPoint();
		myProceedingJoinPoint.setArgs(args);
		myProceedingJoinPoint.setMethod(method);
		myProceedingJoinPoint.setProxyChains(proxyChains);
		myProceedingJoinPoint.setTarget(proxy);

		ProxyChain proxyChain = proxyChains.get(chainsIndex++);
		myProceedingJoinPoint.setChainsIndex(chainsIndex);
		//设置对应执行链节点的参数
		proxyChain.setArgs(new Object[]{myProceedingJoinPoint});
		//执行该节点对应的方法
		return proxyChain.invoker();
	}
}
