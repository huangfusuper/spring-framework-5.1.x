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
public class EmailServiceImpl {
	@Autowired
	private UserServiceImpl userService;

	public UserServiceImpl getUserService() {
		return userService;
	}

	public void setUserService(UserServiceImpl userService) {
		this.userService = userService;
	}
}
