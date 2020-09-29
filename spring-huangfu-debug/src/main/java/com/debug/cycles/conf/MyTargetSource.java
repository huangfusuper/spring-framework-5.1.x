package com.debug.cycles.conf;

import com.debug.cycles.service.UserServiceImpl;
import com.debug.factorypro.beans.UserService;
import org.springframework.aop.TargetSource;

public class MyTargetSource implements TargetSource {
	@Override
	public Class<?> getTargetClass() {
		return UserServiceImpl.class;
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public Object getTarget() throws Exception {
		return null;
	}

	@Override
	public void releaseTarget(Object target) throws Exception {
		System.out.println(target);
	}
}
