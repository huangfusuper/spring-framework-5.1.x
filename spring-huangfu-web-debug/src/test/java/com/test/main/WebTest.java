package com.test.main;

import com.test.controller.DebugController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class WebTest {
	/**
	 * web容器
	 */
	private StaticWebApplicationContext staticWebApplicationContext;
	/**
	 * 请求映射器处理器映射器
	 */
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	/**
	 * 请求映射器适配器
	 */
	private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

	@Before
	public void init() {
		//初始化web容器环境
		staticWebApplicationContext = new StaticWebApplicationContext();
		staticWebApplicationContext.registerBean("debugController", DebugController.class);
		//初始化环境
		staticWebApplicationContext.refresh();
		//初始化请求映射器处理器映射器
		requestMappingHandlerMapping = new RequestMappingHandlerMapping();
		requestMappingHandlerMapping.setApplicationContext(staticWebApplicationContext);
		//处理请求映射器处理器适配器
		requestMappingHandlerAdapter = new RequestMappingHandlerAdapter();
		//必须在调用afterPropertiesSet之前调用
		requestMappingHandlerAdapter.setApplicationContext(staticWebApplicationContext);
		//加载默认的适配器
		requestMappingHandlerAdapter.afterPropertiesSet();

	}

	/**
	 * 获取handlerMethod
	 *
	 * @return 返回handlerMethod
	 * @throws Exception 异常信息
	 */
	private Object getHandler(MockHttpServletRequest request) throws Exception {
		testStartTheProcess();
		HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
		return handlerExecutionChain.getHandler();
	}


	/**
	 * 启动流程调试
	 */
	@Test
	public void testStartTheProcess() {
		requestMappingHandlerMapping.afterPropertiesSet();
	}

	/**
	 * 测试获取HandlerMethod
	 *
	 * @throws Exception 异常信息
	 */
	@Test
	public void testGetHandlerMethod() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test/user");
		System.out.println(getHandler(request));
	}

	/**
	 * 测试handlerMethod适配器的映射规则
	 *
	 * @throws Exception 异常信息
	 */
	@Test
	public void testGetHandlerAdapterSupports() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test/user");
		Object handler = getHandler(request);
		System.out.println(requestMappingHandlerAdapter.supports(handler));
	}

	/**
	 * handler执行器测试
	 * @throws Exception 异常信息
	 */
	@Test
	public void testHandlerInvokerBySimpleParam() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test/user");
		Object handler = getHandler(request);
		request.addParameter("id", "1");
		request.addParameter("name", "huangfu");
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelAndView modelAndView = requestMappingHandlerAdapter.handle(request, response, handler);
		System.out.println(modelAndView);
	}

	/**
	 * handler执行器测试
	 * @throws Exception 异常信息
	 */
	@Test
	public void testHandlerInvokerBySimpleParamGetUser() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test/getUser");
		Object handler = getHandler(request);

		request.addParameter("id", "1");
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelAndView modelAndView = requestMappingHandlerAdapter.handle(request, response, handler);
		System.out.println(modelAndView);
	}
}
