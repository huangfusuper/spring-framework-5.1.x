package com.test.clazz;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2020/11/1 18:54
 */
public abstract class MyServlet {
	public void service(){

		System.out.println("com.test.clazz.MyServlet.service");
		doGet();
	}

	public void doGet(){
		System.out.println("com.test.clazz.MyServlet.doGet");
	}
}
