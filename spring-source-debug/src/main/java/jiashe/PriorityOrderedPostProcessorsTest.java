package jiashe;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;

public class PriorityOrderedPostProcessorsTest implements BeanFactoryPostProcessor, PriorityOrdered {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("----------------PriorityOrderedPostProcessorsTest 高级别----------------------");
		BeanDefinition orderedPostProcessorsTest = beanFactory.getBeanDefinition("orderedPostProcessorsTest");

		//对orderedPostProcessorsTest的BeanDefinition进行修改
		//.....
		//.....
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
