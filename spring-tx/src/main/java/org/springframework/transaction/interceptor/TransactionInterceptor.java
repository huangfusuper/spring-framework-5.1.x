/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.transaction.interceptor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * AOP Alliance MethodInterceptor for declarative transaction
 * management using the common Spring transaction infrastructure
 * ({@link org.springframework.transaction.PlatformTransactionManager}).
 *
 * <p>Derives from the {@link TransactionAspectSupport} class which
 * contains the integration with Spring's underlying transaction API.
 * TransactionInterceptor simply calls the relevant superclass methods
 * such as {@link #invokeWithinTransaction} in the correct order.
 *
 * <p>TransactionInterceptors are thread-safe.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see TransactionProxyFactoryBean
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @see org.springframework.aop.framework.ProxyFactory
 */
@SuppressWarnings("serial")
public class TransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor, Serializable {

	/**
	 * 创建一个新的TransactionInterceptor。
	 * <p>仍然需要设置事务管理器和事务属性。
	 * @see #setTransactionManager
	 * @see #setTransactionAttributes(java.util.Properties)
	 * @see #setTransactionAttributeSource(TransactionAttributeSource)
	 */
	public TransactionInterceptor() {
	}

	/**
	 * 创建一个新的TransactionInterceptor。
	 * @param ptm 默认事务管理器执行实际的事务管理
	 * @param attributes 属性格式的交易属性
	 * @see #setTransactionManager
	 * @see #setTransactionAttributes(java.util.Properties)
	 */
	public TransactionInterceptor(PlatformTransactionManager ptm, Properties attributes) {
		setTransactionManager(ptm);
		setTransactionAttributes(attributes);
	}

	/**
	 * 创建一个新的TransactionInterceptor。
	 * @param ptm 默认事务管理器执行实际的事务管理
	 * @param tas 用于查找交易属性的属性源
	 * @see #setTransactionManager
	 * @see #setTransactionAttributeSource(TransactionAttributeSource)
	 */
	public TransactionInterceptor(PlatformTransactionManager ptm, TransactionAttributeSource tas) {
		setTransactionManager(ptm);
		setTransactionAttributeSource(tas);
	}


	@Override
	@Nullable
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// 计算目标类别：可以为{@code null}。
		// TransactionAttributeSource应该传递给目标类 以及方法（可能来自接口）。
		// //真实目标类
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

		// 适应TransactionAspectSupport的invokeWithinTransaction ...
		//重点分析该方法
		return invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed);

		//以上相当于
//		return invokeWithinTransaction(invocation.getMethod(), targetClass, () ->{
//			return invocation.proceed();
//		});
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------

	private void writeObject(ObjectOutputStream oos) throws IOException {
		// 依靠默认的序列化，尽管此类本身也不携带状态。
		oos.defaultWriteObject();

		// 反序列化超类字段。
		oos.writeObject(getTransactionManagerBeanName());
		oos.writeObject(getTransactionManager());
		oos.writeObject(getTransactionAttributeSource());
		oos.writeObject(getBeanFactory());
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Rely on default serialization, although this class itself doesn't carry state anyway...
		ois.defaultReadObject();

		// Serialize all relevant superclass fields.
		// Superclass can't implement Serializable because it also serves as base class
		// for AspectJ aspects (which are not allowed to implement Serializable)!
		setTransactionManagerBeanName((String) ois.readObject());
		setTransactionManager((PlatformTransactionManager) ois.readObject());
		setTransactionAttributeSource((TransactionAttributeSource) ois.readObject());
		setBeanFactory((BeanFactory) ois.readObject());
	}

}
