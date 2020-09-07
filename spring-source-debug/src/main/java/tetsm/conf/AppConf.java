package tetsm.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import tetsm.MyBeabFactoryAwar;

@Configuration
@ComponentScan("tetsm.service")
public class AppConf {
	@Bean
	public MyBeabFactoryAwar myBeabFactoryAwar(){
		return new MyBeabFactoryAwar();
	}
}
