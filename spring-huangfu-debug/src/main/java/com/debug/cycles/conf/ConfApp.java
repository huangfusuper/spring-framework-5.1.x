package com.debug.cycles.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author huangfu
 */
@ComponentScan("com.debug.cycles.service")
@Configuration
public class ConfApp {

	@Bean
	public MyTargetSource myTargetSource(){
		return new MyTargetSource();
	}

}
