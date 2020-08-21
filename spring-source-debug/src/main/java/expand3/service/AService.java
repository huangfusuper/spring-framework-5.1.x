package expand3.service;

public class AService {

	private Class msg;

	public AService(Class msg) {
		this.msg = msg;
	}


	public void print(){
		System.out.println(msg);
	}
}
