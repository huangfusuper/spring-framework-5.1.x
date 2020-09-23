package com.debug.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author huangfu
 */
@Service
public class UserServiceImpl {

	@Autowired
	private EmailServiceImpl emailService;
}
