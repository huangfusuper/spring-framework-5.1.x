package com.service.impl;

import com.service.AService;
import com.service.BService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BServiceImpl implements BService {
	@Autowired
	private AService aService;

	public void setaService(AService aService) {
		this.aService = aService;
	}

	@Override
	public void print(String msg) {
		System.out.println("BBBBBBBBBBBBBBBBBBBBBB");
	}
}
