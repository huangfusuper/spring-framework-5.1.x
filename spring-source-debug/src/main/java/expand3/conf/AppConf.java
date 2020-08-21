package expand3.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConf {

	@Bean
	public EMyBeanDefinitionRegistryPostProcessor eMyBeanDefinitionRegistryPostProcessor(){
		return new EMyBeanDefinitionRegistryPostProcessor();
	}
}
