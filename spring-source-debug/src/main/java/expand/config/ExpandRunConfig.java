package expand.config;

import expand.service.UserService;
import expand.service.impl.UserServiceImpl;
import expand.test.TestService;
import expand.test.impl.TestServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 扩展配置
 *
 * @author huangfu
 */
@Configuration
//@ComponentScan("expand.service")
public class ExpandRunConfig {
	@Bean
	public TestService testService() {
		System.out.println("testService 被加载");
		return new TestServiceImpl();
	}

	@Bean
	public UserService userService() {
		System.out.println("userService 被加载");
		testService();
		return new UserServiceImpl();
	}
}
