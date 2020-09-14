package tests.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tests.factoryconf.MyImportSelector;

@Configuration
@Import(MyImportSelector.class)
@ComponentScan("tests.factoryconf")
public class AppConf {

}
