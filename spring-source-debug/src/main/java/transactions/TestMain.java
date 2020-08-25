package transactions;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import transactions.conf.SpringConfig;

/**
 * @author huangfu
 */
public class TestMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfig.class);
	}
}
