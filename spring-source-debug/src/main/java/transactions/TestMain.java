package transactions;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import transactions.conf.SpringConfig;
import transactions.service.UserService;

/**
 * @author huangfu
 */
public class TestMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfig.class);
		UserService bean = ac.getBean(UserService.class);
		bean.addUser();
	}
}
