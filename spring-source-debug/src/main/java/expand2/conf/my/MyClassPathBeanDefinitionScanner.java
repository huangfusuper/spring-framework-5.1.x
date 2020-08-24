package expand2.conf.my;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

/**
 * 自定义的扫描器
 * @author huangfu
 */
public class MyClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

	public MyClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		super(registry,false);
	}


	@Override
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		//调用父类的扫描接口  扫描出对应的类
		Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
		processBeanDefinitions(beanDefinitionHolders);
		return beanDefinitionHolders;
	}

	public void registerFilters() {
		//添加一个过滤器  里面对应的方法直接返回true
		addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
	}

	/**
	 * 给对应的bd设置一个bean工厂  那么入后对应的bd就被偷天换日了 创建出来的bean 就是 FactoryBean 通过getObject拦截类的创建过程
	 * @param beanDefinitions bd
	 */
	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		GenericBeanDefinition definition;
		for (BeanDefinitionHolder holder : beanDefinitions) {
			definition = (GenericBeanDefinition) holder.getBeanDefinition();
			String beanClassName = definition.getBeanClassName();
			//这里为构造参数注入的值是一个String类型的  什么鬼？
			definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
			definition.setBeanClass(MyFactoryBean.class);
		}
	}

	/**
	 * 复写父类逻辑  判断这个类的  原始的判断是不允许接口类的存在的  这里返回的是当类为接口的时候返回
	 * @param beanDefinition 要检查的bean定义
	 * @return 当前的bd是否匹配成功
	 */
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		//获取BeanDefinition的详细信息
		AnnotationMetadata metadata = beanDefinition.getMetadata();
		//判断第一是一个完整的类  而且是一个接口的情况下就会被加载  MyBatis是接口的形式嘛
		return metadata.isInterface() && metadata.isIndependent();
	}

	/**
	 * 这个是判断当前的bd是否在对应的容器里面  存在就返回false 不存在就返回true
	 * @param beanName bean的建议名称
	 * @param beanDefinition 相应的bean定义
	 * @return 是否匹配成功
	 */
	@Override
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
		if (super.checkCandidate(beanName, beanDefinition)) {
			return true;
		} else {
			return false;
		}
	}
}
