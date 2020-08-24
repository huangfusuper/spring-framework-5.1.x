package expand3.debug;

import expand3.conf.AppConf;
import expand3.conf.FactoryBeanService;
import expand3.service.AService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DebugConfig {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
		ac.register(AppConf.class);
		ac.refresh();
//		AppConf bean = ac.getBean(AppConf.class);
//		System.out.println( bean );
		FactoryBeanService bean = ac.getBean(FactoryBeanService.class);
		bean.mainStr("你好");
	}
}
