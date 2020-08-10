package expand.service.impl;

import expand.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
	@Override
	public void sendEmail(String userName) {
		System.out.println(String.format("给【%s】发送消息成功",userName));
	}
}
