package com.aop.service.impl;

import com.aop.AopAnnotation;
import com.aop.service.AopService;
import org.springframework.stereotype.Service;

@Service
public class AopServiceImpl implements AopService {

	@AopAnnotation
	@Override
	public String testAop(String msg) throws InterruptedException {
		System.out.println("method invoker。。。。"+msg);
		Thread.sleep(10000);
		return msg;
	}
}
