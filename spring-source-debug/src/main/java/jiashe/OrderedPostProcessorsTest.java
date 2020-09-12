package jiashe;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

public class OrderedPostProcessorsTest implements BeanFactoryPostProcessor, Ordered {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("------------OrderedPostProcessorsTest 低级别---------------------------");
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
