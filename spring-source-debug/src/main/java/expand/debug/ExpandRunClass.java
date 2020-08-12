package expand.debug;

import expand.config.ExpandRunConfig;
import expand.test.impl.TestServiceImpl;
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
		TestServiceImpl bean = annotationConfigApplicationContext.getBean(TestServiceImpl.class);
		bean.testPrint();

	}
}