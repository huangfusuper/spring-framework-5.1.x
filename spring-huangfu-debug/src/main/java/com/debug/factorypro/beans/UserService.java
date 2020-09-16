package com.debug.factorypro.beans;

/**
 * 一个service
 *
 * @author huangfu
 */
public class UserService {
	
	private String name;
	private Class nameClass;


	public UserService(Class nameClass) {
		this.nameClass = nameClass;
	}

	public UserService() {
		System.out.println("初始化了");
	}

	public void print(){
		System.out.println("-----------------");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class getNameClass() {
		return nameClass;
	}

	public void setNameClass(Class nameClass) {
		System.out.println(nameClass);
		this.nameClass = nameClass;
	}
}
