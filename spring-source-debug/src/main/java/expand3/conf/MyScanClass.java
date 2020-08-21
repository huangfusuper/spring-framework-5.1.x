package expand3.conf;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Set;

public class MyScanClass extends ClassPathBeanDefinitionScanner {
	public MyScanClass(BeanDefinitionRegistry registry) {
		super(registry);
	}

	@Override
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		addIncludeFilter((metadataReader,metadataReaderFactory)->true);
		return super.doScan(basePackages);
	}


}
