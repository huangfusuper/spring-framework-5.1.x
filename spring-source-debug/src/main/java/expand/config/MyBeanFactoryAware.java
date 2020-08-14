package expand.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class MyBeanFactoryAware implements BeanFactoryAware {

	private static BeanFactory beanFactory;


	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {


		MyBeanFactoryAware.beanFactory = beanFactory;
	}

	public static void regBean(String beanName , Class beanClass){
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
		AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		beanDefinition.setAutowireMode(2);
		beanDefinition.setPrimary(true);
		beanDefinition.setSynthetic(true);
//        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		beanDefinition.setScope("singleton");
		beanDefinition.setRole(0);
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
		defaultListableBeanFactory.registerBeanDefinition(beanName,beanDefinition);
	}

}
