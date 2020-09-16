package com.debug.factorypro.beans;

import com.debug.factorypro.dao.UserDao;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 我的bean工厂
 *
 * @author huangfu
 * @date 2020年9月14日12:55:32
 */
public class MyFactoryBean implements FactoryBean {
	private Class userClass;

	public void setUserClass(Class userClass) {
		System.out.println(userClass);
		this.userClass = userClass;
	}

	@Override
	public Object getObject() throws Exception {
		Object o = Proxy.newProxyInstance(MyFactoryBean.class.getClassLoader(), new Class[]{userClass}, (proxy, method, args) -> {
			UserDao userDaoImpl = new UserDao() {
				@Override
				public void print() {
					System.out.println("userDaoImpl");
				}
			};
			Object invoke = method.invoke(userDaoImpl);
			return invoke;
		});
		return o;
	}

	@Override
	public Class<?> getObjectType() {
		return userClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
