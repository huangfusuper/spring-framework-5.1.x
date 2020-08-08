package simulation.aop.system;

import java.lang.annotation.*;

/**
 * 模拟AOP通过注解方式添加拦截器
 * @author huangfu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAopAnnotation {
}
