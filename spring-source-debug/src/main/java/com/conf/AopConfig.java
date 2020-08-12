package com.conf;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


@Aspect
public class AopConfig {

	@Pointcut("@annotation(com.conf.annotations.AopAnnotation)")
	public void ann(){}

	@Around("com.conf.AopConfig.ann()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object proceed = joinPoint.proceed();
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
		return proceed;
	}
}
