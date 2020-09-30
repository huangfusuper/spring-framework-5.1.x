/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.beans.factory.config;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * Extension of the {@link InstantiationAwareBeanPostProcessor} interface,
 * adding a callback for predicting the eventual type of a processed bean.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * internal use within the framework. In general, application-provided
 * post-processors should simply implement the plain {@link BeanPostProcessor}
 * interface or derive from the {@link InstantiationAwareBeanPostProcessorAdapter}
 * class. New methods might be added to this interface even in point releases.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see InstantiationAwareBeanPostProcessorAdapter
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

	/**
	 * Predict the type of the bean to be eventually returned from this
	 * processor's {@link #postProcessBeforeInstantiation} callback.
	 * <p>The default implementation returns {@code null}.
	 * @param beanClass the raw class of the bean
	 * @param beanName the name of the bean
	 * @return the type of the bean, or {@code null} if not predictable
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	@Nullable
	default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	/**
	 * 确定要用于给定bean的候选构造函数。
	 * <p>默认实现返回{@code null}。
	 * @param beanClass Bean的原始类（永远{@code null}）
	 * @param beanName bean的名字
	 * @return 候选构造函数，如果未指定，则为{@code null}
	 * @throws org.springframework.beans.BeansException 如果有错误
	 */
	@Nullable
	default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
			throws BeansException {

		return null;
	}

	/**
	 * 获取用于早期访问指定bean的参考，
	 * 通常是为了解决循环参考。
	 * <p>此回调使后处理器有机会及早公开包装器-即在目标Bean实例完全初始化之前。
	 * 公开的对象应等效于* {@link #postProcessBeforeInitialization} /{@link #postProcessAfterInitialization}
	 * 否则将公开的对象。注意，除非后处理器返回与所述后处理回调不同的包装器，
	 * 否则此方法返回的对象将用作Bean引用。换句话说：这些后处理*回调可能最终会公开相同的引用，或者
	 * 从这些后续回调返回原始bean实例（如果受影响的bean的包装器
	 * 已经为调用此方法而构建，
	 * 默认情况下，它将作为最终bean引用公开）。
	 * <p>默认实现按原样返回给定的{@code bean}。
	 * @param bean 原始bean实例
	 * @param beanName 豆的名字
	 * @return 公开为bean引用的对象
	 * (通常将传入的Bean实例作为默认实例)
	 * @throws org.springframework.beans.BeansException 如果有错误
	 */
	default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
