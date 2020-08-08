package simulation.aop.my;

/**
 * @author huangfu
 */
public interface TestService {
	/**
	 * 打印一句话
	 * @param msg 返回信息
	 */
	String print(String msg) throws InterruptedException;

	void sendUser();
}
