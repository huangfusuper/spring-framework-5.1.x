package expand2.conf.my;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * BeanDefinition 的注册器
 * @author huangfu
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		//构建一个bd bd的实质是一个Bean定义注册表后处理器 然后注册到bd中  后面会回调他
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MyBeanDefinitionRegistryPostProcessor.class);
		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.getPropertyValues().add("basePackage","expand2.mapper");
		registry.registerBeanDefinition(generateBaseBeanName(importingClassMetadata,0),beanDefinition);
	}

	private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
		return importingClassMetadata.getClassName() + "#" + MyImportBeanDefinitionRegistrar.class.getSimpleName() + "#" + index;
	}
}
