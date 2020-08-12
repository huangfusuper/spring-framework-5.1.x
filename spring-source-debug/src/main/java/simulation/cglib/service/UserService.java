package simulation.cglib.service;

/**
 * 用户服务
 * @author huangfu
 */
public class UserService {
	private final EmailService emailService;


	public UserService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void userSendEmail(String msg){
		emailService.sendEmail(msg);
	}
}
