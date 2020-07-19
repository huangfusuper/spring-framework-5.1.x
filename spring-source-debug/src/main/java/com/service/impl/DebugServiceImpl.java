package com.service.impl;

import com.service.DebugService;
import org.springframework.stereotype.Service;

/**
 * 调试业务实现
 * @author huangfu
 */
@Service("debugService")
public class DebugServiceImpl implements DebugService {
	public DebugServiceImpl() {
		System.out.println("我被注册了");
	}

	@Override
	public String debugMethod(String name) {
		return name;
	}
}
