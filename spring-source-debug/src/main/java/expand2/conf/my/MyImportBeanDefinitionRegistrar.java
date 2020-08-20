package expand2.conf.my;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author huangfu
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MyBeanDefinitionRegistryPostProcessor.class);
		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.getPropertyValues().add("basePackage","expand2.mapper.UserMapper");
		registry.registerBeanDefinition("userMapper",beanDefinition);
	}
}
