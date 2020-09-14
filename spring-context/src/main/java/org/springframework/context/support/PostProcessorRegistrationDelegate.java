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

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}

	/**
	 * 扫描项目
	 * 调用BeanDefinitionRegistryPostProcessor 将对应的类转成BeanDefinition
	 * 调用 BeanFactoryPostProcessors的回调方法
	 * @param beanFactory bean工厂
	 * @param beanFactoryPostProcessors 手动提供的后置处理器
	 */
	public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// 如果有的话，首先调用BeanDefinitionRegistryPostProcessors。
		Set<String> processedBeans = new HashSet<>();
		//默认使用的是DefaultListableBeanFactory工厂对象 所以i这个判断一定会进入进来
		if (beanFactory instanceof BeanDefinitionRegistry) {
			//事实上就是Bean工厂
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			//存放程序员自己手动提供给Spring的后置处理器
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			//存放执行该过程中寻找到的 BeanDefinitionRegistryPostProcessor
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			//循环遍历bean工厂后处理器 但是这个的debug的对象确实为Null不知道为什么  事实上它并不会进入到这里
			//这个是扫描用户自己手动添加的一些BeanFactoryPostProcessors
			//事实上 我们很少会对这里进行更改，只有在对接或者开发第三方组件的时候可能会手动的设置一个后置处理器
			//正常情况下极少能够使用到这种情况
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				//这个判断就是为了保证spring自己的扫描处理器先执行  因为此时spring还没有完成扫描
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor = (BeanDefinitionRegistryPostProcessor) postProcessor;
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					registryProcessors.add(registryProcessor);
				}
				else {
					//自己定义的内助处理器
					regularPostProcessors.add(postProcessor);
				}
			}

			// 不要在这里初始化FactoryBeans：我们需要保留所有常规bean
			// 未初始化，让Bean工厂后处理器对其应用！
			// 在实现的BeanDefinitionRegistryPostProcessor之间分开
			// PriorityOrdered，Ordered和其他。
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// 首先，调用实现PriorityOrdered(排序接口)的BeanDefinitionRegistryPostProcessors。 这是获取内置bean工厂后置处理器的beanName
			//查出所有实现了BeanDefinitionRegistryPostProcessor接口的bean名称
			//调用了一次BeanDefinitionRegistryPostProcessor子类  PriorityOrdered
			//获取 BeanDefinitionRegistryPostProcessor 的子类 事实上 这里只有一个叫做  ConfigurationClassPostProcessor 他实现了 PriorityOrdered接口
			//BeanFactoryPostProcessor 也就是 ConfigurationClassPostProcessor 会被添加到容器里面
			String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				//判断当前这个类是不是实现了PriorityOrdered接口
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					//getBean会提前走生命周期
 					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
 					//将这个已经处理过的添加到集合里面
					//为什么要天机哀悼集合里面呢？因为本身他就属于 BeanDefinitionRegistryPostProcessor 是 BeanFactoryPostProcessor的子类
					//那么肯定 在执行BeanFactoryPostProcessor的回调的时候，他还会再次的被获取执行
					//索性 Spring为了节省效率，避免这部分 BeanDefinitionRegistryPostProcessor类被重复 获取，就在完全调用了BeanDefinitionRegistryPostProcessor类之后
					//将这一部分的接口直接给执行了也就是BeanDefinitionRegistryPostProcessor的BeanFactoryPostProcessor的回调方法是优先于直接实现BeanFactoryPostProcessor方法的
					//既然在执行BeanFactoryPostProcessor之前就执行了对应的方法回调，那么肯定，执行BeanFactoryPostProcessor的时候要把之前已经执行过的过滤掉
					//故而会将BeanDefinitionRegistryPostProcessor存储起来，后续执行BeanFactoryPostProcessor会跳过集合里面的类
					processedBeans.add(ppName);
				}
			}
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			//见该处理器添加到对应的已注册集合里面 方面后面直接回调他们的父类方法也就是  BeanFactoryPostProcessors方法
			registryProcessors.addAll(currentRegistryProcessors);
			//调用Bean定义注册表后处理器  这里是真正的读取类的bd的一个方法 ConfigurationClassPostProcessor 第一次调用beanFactory 后置处理器
			//这里调用ConfigurationClassPostProcessor后置处理器会注册一个后置处理器，下面进行回调
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			//清空当前这个处理器
			currentRegistryProcessors.clear();

			// 接下来，调用实现Ordered的BeanDefinitionRegistryPostProcessors。   Ordered
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				//判断当前这个类是不是实现了Ordered接口
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					//getBean会提前走生命周期
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			//见该处理器添加到对应的已注册集合里面 方面后面直接回调他们的父类方法也就是  BeanFactoryPostProcessors方法
			registryProcessors.addAll(currentRegistryProcessors);
			//调用当前的BeanDefinitionRegistryPostProcessor 回调方法
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			//清空当前这个处理器
			currentRegistryProcessors.clear();

			// 最后，调用所有其他BeanDefinitionRegistryPostProcessor，直到没有其他的出现。
			boolean reiterate = true;
			//这里为什么是死循环呢？
			//因为 BeanDefinitionRegistryPostProcessor 本身进行回调的时候会手动注册一些特殊的类，例如再次注册一个BeanDefinitionRegistryPostProcessor
			//类，可能手动注册的类里面还有，像套娃一样，故而需要进行不断的循环迭代获取，从而达到遍历全部的 BeanDefinitionRegistryPostProcessor的目的
			while (reiterate) {
				reiterate = false;
				//获取所有的BeanDefinitionRegistryPostProcessor接口的实现类
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				//遍历这些BeanDefinitionRegistryPostProcessor类
				for (String ppName : postProcessorNames) {
					//如果它不存在与这个集合里面，证明没有被上面处理过，就不会被跳过，这里主要是解决重复执行的情况
					if (!processedBeans.contains(ppName)) {
						//添加到对应的当前处理器集合里面
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						//添加到已处理集合里面
						processedBeans.add(ppName);
						//将扫描标识为true 准备下次执行
						reiterate = true;
					}
				}
				//排序
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				//注册到注册集合里面，便于后修直接回调父类
				registryProcessors.addAll(currentRegistryProcessors);
				//开始执行这些方法
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				//清空本次执行的处理集合
				currentRegistryProcessors.clear();
			}

			// 现在，调用到目前为止已处理的所有处理器的postProcessBeanFactory回调。
			//BeanDefinitionRegistryPostProcessor 是 BeanFactoryPostProcessor
			//目的就是为了避免重复获取
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			//常规的 普通的工厂后置处理器
			//程序员手动提供给Spring的BeanFactory  beanFactory.addBeanFactoryPostProcessor(new MyBeanFactoryPostProcessor())
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		} else {
			// 调用在上下文实例中注册的工厂处理器。
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// 不要在这里初始化FactoryBeans：我们需要保留所有常规bean
		// 未初始化，让Bean工厂后处理器对其应用！
		//这里是真正获取容器内部所有的beanFactory的后置处理器
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();

//		List<String> orderedPostProcessorNames = new ArrayList<>();
//		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			//上面是否已经被执行过了，执行过的直接跳过
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			//添加 实现了PriorityOrdered的BeanFactoryPostProcessors
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {

				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			//添加 实现了Ordered的BeanFactoryPostProcessors
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
				//orderedPostProcessorNames.add(ppName);
			}
			else {
				//添加剩余的
				nonOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
				//nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// 首先，调用实现PriorityOrdered的BeanFactoryPostProcessors。
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		//首先回调实现了PriorityOrdered的BeanFactoryPostProcessors
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// 接下来，调用实现Ordered的BeanFactoryPostProcessors。
		//List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		//for (String postProcessorName : orderedPostProcessorNames) {
			//getBean可以进行提前实例化进入生命周期
			//orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		//}
		//sortPostProcessors(orderedPostProcessors, beanFactory);
		// 接下来，调用实现Ordered的BeanFactoryPostProcessors。
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		//最后，调用所有其他BeanFactoryPostProcessors。
		//List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		//for (String postProcessorName : nonOrderedPostProcessorNames) {
			//getBean可以进行提前实例化进入生命周期
			//nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		//}
		//这里执行的自定义的bean工厂的后置处理器
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// 清除缓存的合并bean定义，因为后处理器可能具有修改了原始元数据，例如替换值中的占位符...
		beanFactory.clearMetadataCache();
	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// 首先，注册实现PriorityOrdered的BeanPostProcessor。
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// 接下来，注册实现Ordered的BeanPostProcessor。
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// 现在，注册所有常规BeanPostProcessor。
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				//内部后处理器
				internalPostProcessors.add(pp);
			}
		}
		//开始执行真正的注册逻辑
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// 最后，重新注册所有内部BeanPostProcessor。
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// 重新注册用于将内部bean检测为ApplicationListener的后处理器，
		//将其移动到处理器链的末尾（用于拾取代理等）。
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * 调用给定的BeanFactoryPostProcessor bean。
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
