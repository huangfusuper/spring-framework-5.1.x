package com.debug.test;

import com.debug.test.config.AppConf;
import com.debug.test.service.UserServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author huangfu
 */
public class TestMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConf.class);
		//通过Spring管理的的对象
		UserServiceImpl bean = ac.getBean(UserServiceImpl.class);
		//自己手动创建的对象
		UserServiceImpl userService = new UserServiceImpl();
	}
}
