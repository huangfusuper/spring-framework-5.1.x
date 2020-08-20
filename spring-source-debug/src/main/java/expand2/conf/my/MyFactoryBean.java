package expand2.conf.my;

import expand2.handler.MyInvocationHandler;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author huangfu
 */
public class MyFactoryBean<T> implements FactoryBean {

	private Class<T> mapperInterface;

	public MyFactoryBean(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(MyFactoryBean.class.getClassLoader(),new Class[]{mapperInterface},new MyInvocationHandler());
	}

	@Override
	public Class<?> getObjectType() {
		return this.mapperInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
