package com.service.impl;

import com.service.AService;
import com.service.BService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AServiceImpl implements AService {

	@Autowired
	private BService bService;

	public void setbService(BService bService) {
		this.bService = bService;
	}

	@Override
	public void print() {
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA");
	}
}
