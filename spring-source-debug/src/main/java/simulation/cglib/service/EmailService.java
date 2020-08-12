package simulation.cglib.service;

/**
 * 邮件服务
 * @author huangfu
 */
public class EmailService {

	public void sendEmail(String msg){
		System.out.println(String.format("发送成功,消息为:%s",msg));
	}
}
