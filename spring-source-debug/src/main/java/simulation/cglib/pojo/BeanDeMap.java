package simulation.cglib.pojo;

import java.lang.reflect.Method;

/**
 * @author huangfu
 */
public class BeanDeMap {
	private String beanFactoryName;
	/**
	 * 原始类的class
	 */
	private Class aClass;


	private Method method;

	private Class beanFactoryClass;

	public String getBeanFactoryName() {
		return beanFactoryName;
	}

	public void setBeanFactoryName(String beanFactoryName) {
		this.beanFactoryName = beanFactoryName;
	}

	public Class getBeanFactoryClass() {
		return beanFactoryClass;
	}

	public void setBeanFactoryClass(Class beanFactoryClass) {
		this.beanFactoryClass = beanFactoryClass;
	}

	public Class getaClass() {
		return aClass;
	}

	public void setaClass(Class aClass) {
		this.aClass = aClass;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
}
