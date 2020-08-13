package simulation.cglib.config;

import simulation.cglib.annotations.MyBean;
import simulation.cglib.service.EmailService;
import simulation.cglib.service.UserService;

public class AppConfig {
	@MyBean
	public EmailService emailService(){
		System.out.println("-----emailService参与实例化----");
		return new EmailService();
	}

	@MyBean
	public UserService userService(){
		System.out.println("-----userService参与实例化----");
		EmailService emailService = emailService();
		return new UserService(emailService);
	}

}
