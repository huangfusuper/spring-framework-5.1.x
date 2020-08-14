package expand.debug;

import expand.config.ExpandRunConfig;
import expand.config.MyBeanFactoryAware;
import expand.test.BeanFactoryService;
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
		MyBeanFactoryAware.regBean("testBean", BeanFactoryService.class);
		System.out.println(annotationConfigApplicationContext.getBean(BeanFactoryService.class).print("sadasda"));
	}
}