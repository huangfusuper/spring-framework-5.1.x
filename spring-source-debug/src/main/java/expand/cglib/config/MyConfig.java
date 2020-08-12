package expand.cglib.config;

import expand.cglib.annotations.MyBean;
import expand.cglib.service.UserService;

public class MyConfig {

	@MyBean
	public UserService userService(){
		return new UserService();
	}
}
