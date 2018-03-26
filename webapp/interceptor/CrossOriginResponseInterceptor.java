package com.shixi.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.shixi.web.common.AYouConfig;
import com.shixi.web.tools.HttpUtils;
import com.shixi.web.tools.SysUtils;

public class CrossOriginResponseInterceptor implements HandlerInterceptor {

	Logger logger = LoggerFactory.getLogger(CrossOriginResponseInterceptor.class);

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		request.setAttribute("backAuth", 1);
		System.out.println("put backAuth value->1");
		
		if (request.getMethod().toLowerCase().equals("get")) {
			if (logger.isDebugEnabled()) {
				logger.debug("前端" + request.getMethod() + "请求：url=" + request.getRequestURL() + " 参数：" + HttpUtils.parseParamsToString(request));
			}
		}
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE,PUT");
		response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
//		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers",
				"Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
		
		if (!AYouConfig.isProdEnv()) {
			response.addHeader("_ayou_server",  request.getLocalAddr() + ":" + request.getLocalPort());
		}
		
		
		if (request.getMethod().equalsIgnoreCase("options")) {
			response.setStatus(204);
			return false;
		}
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
