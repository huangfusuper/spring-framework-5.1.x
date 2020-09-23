package com.debug.cycles;

import com.debug.cycles.conf.ConfApp;
import com.debug.cycles.service.EmailServiceImpl;
import com.debug.cycles.service.UserServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestMail {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ConfApp.class);
		UserServiceImpl bean = ac.getBean(UserServiceImpl.class);
	}
}
