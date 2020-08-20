package expand2.conf.my;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Set;

public class MyClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
	private Class<?> markerInterface;

	public MyClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		super(registry,false);
	}


	@Override
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
			@Override
			protected boolean matchClassName(String className) {
				return false;
			}
		});
		addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
		Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
		processBeanDefinitions(beanDefinitionHolders);
		return beanDefinitionHolders;
	}

	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		GenericBeanDefinition definition;
		for (BeanDefinitionHolder holder : beanDefinitions) {
			definition = (GenericBeanDefinition) holder.getBeanDefinition();
			String beanClassName = definition.getBeanClassName();
			definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
			definition.setBeanClass(MyFactoryBean.class);
		}
	}

	public void setMarkerInterface(Class<?> markerInterface) {
		this.markerInterface = markerInterface;
	}
}
