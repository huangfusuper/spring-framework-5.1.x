package web.mvc.handler;

import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 第二种方式HttpRequestHandler
 *
 * @author huangfu
 */
public class MyHttpRequestHandler implements HttpRequestHandler {
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().println("皇甫科星");
	}
}
