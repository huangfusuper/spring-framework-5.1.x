package web.mvc.interceptor;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 拦截器
 * @author huangfu
 */
public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("前置拦截");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("后置拦截");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("完成处理拦截");
    }


	public static void main(String[] args) throws NoSuchMethodException {
		Method preHandle = MyInterceptor.class.getMethod("preHandle", HttpServletRequest.class, HttpServletResponse.class, Object.class);
		LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] parameterNames = localVariableTableParameterNameDiscoverer.getParameterNames(preHandle);
		for (int i = 0; i < parameterNames.length; i++) {
			System.out.println(parameterNames[i]);
		}
	}
}
