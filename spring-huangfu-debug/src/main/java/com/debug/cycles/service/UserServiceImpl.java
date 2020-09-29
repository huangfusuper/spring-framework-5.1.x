package com.debug.cycles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author huangfu
 * @date 2020年9月23日09:06:14
 */
@Service

public class UserServiceImpl {
	private EmailServiceImpl emailService;


	@Autowired
	public void setEmailService(EmailServiceImpl emailService) {
		this.emailService = emailService;
	}

	public void send(){
		emailService.send();
	}


}
