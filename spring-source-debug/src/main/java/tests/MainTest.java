package tests;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tests.conf.AppConf;

public class MainTest {


	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConf.class);

	}
}
