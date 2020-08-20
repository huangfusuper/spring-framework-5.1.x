package expand2.debug;

import expand2.conf.MyConf;
import expand2.mapper.UserMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyDebug {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(MyConf.class);
		UserMapper bean = ac.getBean(UserMapper.class);
		System.out.println(bean.selectId("张三"));

	}
}
