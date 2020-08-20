package expand2.conf.my;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * BeanDefinitionRegistryPostProcessor他是BeanFactoryPostProcessor子类  属于一个bena工厂后置处理器  他会被spring回调 postProcessBeanDefinitionRegistry
 * @author huangfu
 */
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	/**
	 * 这个是前面通过参数注入的一个包 getPropertyValues  类似于xml的注入方式
	 */
	private String basePackage;


	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		/**
		 * 构建一个扫描器
		 */
		MyClassPathBeanDefinitionScanner scanner = new MyClassPathBeanDefinitionScanner(registry);
		/**
		 * 注册一个回调  后面调用到判断的时候他直接返回为true了  调用时机暂时不知道  后面会说
		 */
		scanner.registerFilters();
		/**
		 * 开始扫描自定义包路径下的类
		 */
		scanner.scan(StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
}
