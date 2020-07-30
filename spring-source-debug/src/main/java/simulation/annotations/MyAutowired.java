package simulation.annotations;

import java.lang.annotation.*;

/**
 * @author huangfu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface MyAutowired {
}
