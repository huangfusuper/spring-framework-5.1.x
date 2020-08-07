package com.debug;

import com.aop.SpringAopConfig;
import com.aop.service.AopService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * spring debug
 * @author huangfu
 */
public class SpringDebug {
	public static void main(String[] args) throws InterruptedException {
		AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(SpringAopConfig.class);
		System.out.println(app.getBean(AopService.class).testAop("asd"));
	}
}
