package expand.config;

import expand.service.UserService;
import expand.service.impl.UserServiceImpl;
import expand.test.TestService;
import expand.test.impl.TestServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 全局配置类
 *
 * @author huangfu
 */
@Configuration
public class ExpandRunConfig {
	@Bean
	public TestService testService() {
		return new TestServiceImpl();
	}

	@Bean
	public UserService userService() {
		testService();
		return new UserServiceImpl();
	}

	@Bean
	public MyBeanFactoryAware myBeanFactoryAware(){
		return new MyBeanFactoryAware();
	}
}
