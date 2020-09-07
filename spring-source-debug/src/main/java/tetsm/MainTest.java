package tetsm;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tetsm.conf.AppConf;
import tetsm.service.TestService;

public class MainTest {


	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConf.class);

		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(TestService.class);

		AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		MyBeabFactoryAwar.registerBean("testService", beanDefinition);
		TestService testService = ac.getBean(TestService.class);
		TestService testService2 = ac.getBean(TestService.class);
		MyBeabFactoryAwar.removeBean("testService");
		TestService testService1 = ac.getBean(TestService.class);
	}
}
