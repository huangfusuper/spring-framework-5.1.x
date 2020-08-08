package simulation.aop.my;

import simulation.aop.system.MyAopAnnotation;
import simulation.aop.system.MyAround;
import simulation.aop.system.MyProceedingJoinPoint;

import java.lang.reflect.InvocationTargetException;

/**
 * 定义一个切面
 * @author huangfu
 */
public class MyAspect {

	/**
	 * 拦截所有方法上携带  MyAopAnnotation 注解的方法
	 * @param joinPoint
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@MyAround(targetClass = MyAopAnnotation.class)
	public Object testAspect(MyProceedingJoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {
		long startTime = System.currentTimeMillis();
		//方法放行
		Object proceed = joinPoint.proceed(joinPoint.getArgs());
		long endTime = System.currentTimeMillis();
		System.out.println("总共用时："+(endTime - startTime));
		return proceed;
	}


	/**
	 * 拦截所有方法上携带  MyAopAnnotation 注解的方法
	 * @param joinPoint
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@MyAround(targetClass = MyAopAnnotation.class)
	public Object testAspect2(MyProceedingJoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {
		long startTime = System.currentTimeMillis();
		//方法放行
		Object proceed = joinPoint.proceed(joinPoint.getArgs());
		long endTime = System.currentTimeMillis();
		System.out.println("总共用时："+(endTime - startTime));
		return proceed;
	}
}
