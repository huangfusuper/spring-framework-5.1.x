package com.debug;

import com.conf.SpringDebugConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * spring debug
 * @author huangfu
 */
public class SpringDebug {
	public static void main(String[] args) throws InterruptedException {
		AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(SpringDebugConfig.class);
	}
}
