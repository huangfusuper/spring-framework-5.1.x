package simulation.cglib.config;

import simulation.cglib.annotations.MyBean;
import simulation.cglib.service.EmailService;
import simulation.cglib.service.UserService;

public class AppConfig {
	@MyBean
	public EmailService emailService(){
		return new EmailService();
	}

	@MyBean
	public UserService userService(){
		EmailService emailService = emailService();
		return new UserService(emailService);
	}

}
