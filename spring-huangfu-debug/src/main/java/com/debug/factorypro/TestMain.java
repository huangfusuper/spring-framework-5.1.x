package com.debug.factorypro;

import com.debug.factorypro.beans.UserService;
import com.debug.factorypro.conf.AppConf;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 测试主函数
 *
 * @author huangfu
 * @date 2020年9月14日12:51:54
 */
public class TestMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConf.class);

		UserService bean = ac.getBean(UserService.class);
		UserService bean1 = ac.getBean(UserService.class);
	}
}
