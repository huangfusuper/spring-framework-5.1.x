package expand3.conf;

import org.springframework.beans.factory.FactoryBean;

public class FactoryBeanTest implements FactoryBean {

	@Override
	public Object getObject() throws Exception {
		return new FactoryBeanService();
	}

	@Override
	public Class<?> getObjectType() {
		return FactoryBeanService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}