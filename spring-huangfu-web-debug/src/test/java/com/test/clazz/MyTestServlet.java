package com.test.clazz;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/11/1 18:55
 */
public class MyTestServlet extends MyServlet {
	@Override
	public void service() {
		super.service();
	}

	@Override
	public void doGet() {
		System.out.println("my---com.test.clazz.MyTestServlet.doGet");
	}

	public static void main(String[] args) {
		MyServlet myServlet = new MyTestServlet();
		myServlet.service();
	}
}
