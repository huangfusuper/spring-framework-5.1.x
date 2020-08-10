package expand.debug;

import expand.config.ExpandRunConfig;
import expand.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author huangfu
 */
public class ExpandRunClass {
	/**
	 * 注册没有扫描的bean
	 * @param args
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(ExpandRunConfig.class);
		ExpandRunConfig runConfig = annotationConfigApplicationContext.getBean(ExpandRunConfig.class);

		System.out.println(runConfig.getClass());

	}
}
