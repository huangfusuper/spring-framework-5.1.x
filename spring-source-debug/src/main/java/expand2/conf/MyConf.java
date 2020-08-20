package expand2.conf;

import expand2.conf.my.MyImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MyImportBeanDefinitionRegistrar.class)
public class MyConf {
}
