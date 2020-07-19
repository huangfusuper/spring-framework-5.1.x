package com.service.impl;

import com.conf.annotations.CustomizeScannerAnnotation;
import com.service.CustomizeScannerClass;

/**
 * @author huangfu
 */
@CustomizeScannerAnnotation
public class CustomizeScannerClassImpl implements CustomizeScannerClass {

	@Override
	public String customizeType(String name) {
		return name + "哈哈哈";
	}
}
