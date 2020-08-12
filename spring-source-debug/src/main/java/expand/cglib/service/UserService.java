package expand.cglib.service;

import expand.cglib.annotations.MyBean;

public class UserService {


	@MyBean
	public String print(){
		return "print invoker";
	}
}
