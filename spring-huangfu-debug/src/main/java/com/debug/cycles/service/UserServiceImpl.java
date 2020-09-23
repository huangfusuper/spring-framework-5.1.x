package com.debug.cycles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author huangfu
 * @date 2020年9月23日09:06:14
 */
@Service
@Scope("prototype")
public class UserServiceImpl {
	@Autowired
	private EmailServiceImpl emailService;

	public void setEmailService(EmailServiceImpl emailService) {
		this.emailService = emailService;
	}
}
