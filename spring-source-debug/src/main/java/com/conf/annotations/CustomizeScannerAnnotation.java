package com.conf.annotations;

import java.lang.annotation.*;

/**
 * 自定义扫描注解  Spring将该注解当作组件扫描
 * @author huangfu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomizeScannerAnnotation {
}
