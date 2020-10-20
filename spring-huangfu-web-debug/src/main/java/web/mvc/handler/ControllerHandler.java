package web.mvc.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author huangfu
 *
 * Controller类型的handler   属于第一种映射器类型
 * 通过继承controller来实现一种对servlet的封装
 *
 * Controller类型的处理器
 */
@Component("/controller")
public class ControllerHandler implements Controller {
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("userView");
		modelAndView.addObject("name","皇甫科星");
		return modelAndView;
	}
}
