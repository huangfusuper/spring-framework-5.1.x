package com.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@ComponentScan("com.aop.service")
public class SpringAopConfig {
	@Bean
	public AspectJDebug aspectJDebug(){
		return new AspectJDebug();
	}
}
