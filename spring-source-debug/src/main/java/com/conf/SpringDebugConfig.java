package com.conf;

import com.conf.annotations.CustomizeScannerAnnotation;
import com.conf.annotations.MyBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * spring调试配置类
 * @author huangfu
 */
@Configuration
@ComponentScan(basePackages = {"com.service","com.aop"})
public class SpringDebugConfig {

	//@Bean
	public MyBeanPostProcessor myBeanPostProcessor(){
		return new MyBeanPostProcessor();
	}

	//@Bean
	public MyBeanFactoryPostProcessor myBeanFactoryPostProcessor() {
		return new MyBeanFactoryPostProcessor();
	}
	/**
	 * 使用自定义注解的扫描器
	 * @param applicationContext
	 * @return
	 *
	 *
	 */
	@Bean
	public ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner(ApplicationContext  applicationContext){
		AnnotationConfigApplicationContext annotationConfigApplicationContext = (AnnotationConfigApplicationContext) applicationContext;
		ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(annotationConfigApplicationContext);
		MyScanner myScanner = new MyScanner(annotationConfigApplicationContext);
		myScanner.addIncludeFilter((e,e1) ->true);
		myScanner.scan("com.service");

		return myScanner;
	}
}
