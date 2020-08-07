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
		Object[] args = joinPoint.getArgs();
		Object proceed = joinPoint.proceed(args);
		long endTime = System.currentTimeMillis();
		System.out.println("method invoker time:"+(endTime-startTime));
		return proceed;
	}

}
