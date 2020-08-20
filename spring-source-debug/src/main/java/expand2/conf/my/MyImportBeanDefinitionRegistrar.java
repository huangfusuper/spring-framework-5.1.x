package expand2.conf.my;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 *
 * ImportBeanDefinitionRegistrar 他的调用时机是在解析之后
 * 解析配置类之后会吧Import里面的解析为bd 然后注册到一个map中
 * 后面调用 {@link org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsFromRegistrars(java.util.Map)}
 * 注册进来并进行最终的回调 registerBeanDefinitions 回调之后这里有重新祖册了 MyBeanDefinitionRegistryPostProcessor 点进去看注释！
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
