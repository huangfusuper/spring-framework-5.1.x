package com.aop.impl;

import com.aop.AopService;
import com.conf.annotations.AopAnnotation;
import org.springframework.stereotype.Component;
public class AopServiceImpl implements AopService {

	@AopAnnotation
	@Override
	public void test() throws InterruptedException {
		System.out.println("----方法逻辑--------");
		Thread.sleep(10000);
		System.out.println("-----结束");
	}
}
