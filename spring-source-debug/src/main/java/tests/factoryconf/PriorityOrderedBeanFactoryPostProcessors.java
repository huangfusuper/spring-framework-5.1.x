package tests.factoryconf;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

/**
 * 高级别的BeanPostProcessors
 *
 * @author huangfu
 * @date 2020年9月11日09:15:35
 */
@Component
public class PriorityOrderedBeanFactoryPostProcessors implements BeanFactoryPostProcessor, PriorityOrdered {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition) beanFactory.getBeanDefinition("myBeanFactoryPostProcessor");
		System.out.println("-----------MyBeanPostProcessors -------------------");
		//这里就是修改了myBeanFactoryPostProcessor的BeanClass 不具体写了
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
