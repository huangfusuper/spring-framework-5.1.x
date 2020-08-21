package expand3.conf;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.Set;

public class EMyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		MyScanClass myScanClass = new MyScanClass(registry);
		Set<BeanDefinitionHolder> beanDefinitionHolders = myScanClass.doScan("expand3.service");
		beanDefinitionHolders.forEach(e ->{
			BeanDefinition beanDefinition = e.getBeanDefinition();
			beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("expand3.debug.BService");
		});
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}
}
