package tetsm;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

public class MyBeabFactoryAwar implements BeanFactoryAware {
	private static BeanFactory beanFactory = null;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		MyBeabFactoryAwar.beanFactory = beanFactory;
	}


	public static void registerBean(String beanName,BeanDefinition beanDefinition){
		((DefaultListableBeanFactory)beanFactory).registerBeanDefinition(beanName, beanDefinition);
	}

	public static void removeBean(String beanName){
		((DefaultListableBeanFactory)beanFactory).destroySingleton(beanName);
	}
}
