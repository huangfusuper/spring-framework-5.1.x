package expand3.debug;

import expand3.conf.AppConf;
import expand3.service.AService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DebugConfig {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
		ac.register(AppConf.class);
		ac.refresh();

		AService bean = ac.getBean(AService.class);
		bean.print();
	}
}
