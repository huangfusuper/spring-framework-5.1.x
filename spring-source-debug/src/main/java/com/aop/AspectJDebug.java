package com.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectJDebug {
	@Pointcut("@annotation(com.aop.AopAnnotation)")
	private void anyPublicOperation() {}

	@Around("com.aop.AspectJDebug.anyPublicOperation()")
	public Object doAccessCheck(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		System.out.println("start Time :" + startTime);
		Object[] args = joinPoint.getArgs();
		Object proceed = joinPoint.proceed(args);
		long endTime = System.currentTimeMillis();
		System.out.println("method invoker time:"+(endTime-startTime));
		return proceed;
	}



	@Around("com.aop.AspectJDebug.anyPublicOperation()")
	public Object doAccessCheck222(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println("222222222222222222");
		Object[] args = joinPoint.getArgs();
		Object proceed = joinPoint.proceed(args);
		System.out.println("222222222222222222");
		return proceed;
	}

}
