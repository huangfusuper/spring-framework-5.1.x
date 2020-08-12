package simulation.cglib.annotations;

import java.lang.annotation.*;

/**
 * 模仿 @Bean
 * @author huangfu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyBean {
}
