package com.debug;

import com.conf.SpringDebugConfig;
import com.service.CustomizeScannerClass;
import com.service.DebugService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * spring debug
 * @author huangfu
 */
public class SpringDebug {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(SpringDebugConfig.class);
		DebugService debugService = app.getBean(DebugService.class);
		CustomizeScannerClass bean = app.getBean(CustomizeScannerClass.class);
		System.out.println(debugService.debugMethod("嘿嘿"));
		System.out.println(bean.customizeType("皇甫"));
	}
}
