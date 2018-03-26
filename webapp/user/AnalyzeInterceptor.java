package com.shixi.web.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.shixi.web.authorize.UserContext;
import com.shixi.web.authorize.cookie.CookieFactory;
import com.shixi.web.authorize.cookie.EmptyCookieFactory;
import com.shixi.web.authorize.cookie.UserCookieContainer;
import com.shixi.web.authorize.redis.RedisCacheIoc;
import com.shixi.web.authorize.redis.RedisCacheManager;
import com.shixi.web.backmanage.authorize.BackUserContext;
import com.shixi.web.backmanage.authorize.BackUserCookieContainer;
import com.shixi.web.common.Constants;
import com.shixi.web.model.AUUser;
import com.shixi.web.model.UserInfo;
import com.shixi.web.model.user.BackUserInfo;
import com.shixi.web.service.UserService;
import com.shixi.web.tools.StringUtil;

/**
 * 
 * @author wuyingjie
 * @date 2017年6月13日
 */

public class AnalyzeInterceptor implements HandlerInterceptor{

	Logger logger = LoggerFactory.getLogger(AnalyzeInterceptor.class);
	
	UserContext userContext;
	
	BackUserContext backUserContext;
	
	@Autowired
	UserService userService;
	@Resource(name=RedisCacheIoc.BACK_USER_CACHE)
	RedisCacheManager<String, BackUserInfo> backUserCache;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Object o = request.getAttribute("backAuth");
		int backAuth = StringUtil.toInteger(o.toString(), 0);
		System.out.println("get backAuth value->"+backAuth);
		
		Cookie[] cookies = request.getCookies();
		Long userId = null;
		String userSessionCookie = null;
		String nativeIdCookie = null;
		String backUserCookie = null;
		String backUsernameCookie = null;
		
		if (cookies != null && cookies.length > 0) {
			for(Cookie c : cookies) {
				if (c.getName().equals(CookieFactory.COOKIE_KEY_USER_SESSION)) {
					userSessionCookie = c.getValue();
				} else if (c.getName().equals(CookieFactory.COOKIE_KEY_NATIVE_ID)) {
					nativeIdCookie = c.getValue();
				} else if (c.getName().equals(CookieFactory.COOKIE_KEY_BACK_USER)) {
					backUserCookie = c.getValue();
				} else if (c.getName().equals(CookieFactory.COOKIE_KEY_BACK_USERNAME)) {
					backUsernameCookie = c.getValue();
				}
			}
		}
		
		if (!StringUtil.isEmpty(userSessionCookie)) {
			UserCookieContainer container = null;
			if ((container = UserCookieContainer.resolveUserCookie(userSessionCookie)) != null) {
				setUser(container.getUser(), container.getUuid());
				userId = container.getUser().getUserId();
				
				//如果超过3分钟更新
				updateUserCookie(container, response);
				
				if (StringUtil.isEmpty(nativeIdCookie)) {
					response.addCookie(CookieFactory.getNativeIdCookie(userId));
					UserInfo userInfo = userService.getUserInfoById(userId);
					response.addCookie(CookieFactory.getUsernameCookie(userInfo.getNickname()));
					response.addCookie(CookieFactory.getUserIconCookie(userInfo.getIcon()));
				}
				
			} else {
				response.addCookie(EmptyCookieFactory.getEmptyUserCookie());
			}
		}
		
		
		if (userId == null) {
			if (userContext != null) {
				userContext.close();
			}
			
			if (!StringUtil.isEmpty(nativeIdCookie)) {
				response.addCookie(EmptyCookieFactory.getEmptyNativeIdCookie());
			}
		}
		
		
		BackUserCookieContainer backUserCookieContainer =
				BackUserCookieContainer.resolveBackUserCookie(backUserCookie);
		if (backUserCookieContainer != null) {
			BackUserInfo backUser = backUserCache.get(backUserCookieContainer.getUuid());
			
			if (backUser != null) {
				setBackUser(backUser, backUserCookieContainer.getUuid());
				updateBackUserStatus(backUserCookieContainer, backUser, response);
				if (StringUtil.isEmpty(backUsernameCookie)) {
					response.addCookie(CookieFactory.getBackUsernameCookie(backUser.getUsername()));
				}
			}
			
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (userContext != null) {
			userContext.close();
		}
		
		if (backUserContext != null) {
			backUserContext.close();
		}
	}
	
	private void setUser(AUUser user, String uuid){
		userContext = new UserContext(user, uuid);
	}
	
	private void setBackUser(BackUserInfo backUser, String uuid) {
		backUserContext = new BackUserContext(backUser, uuid);
	}
	
	private void updateUserCookie(UserCookieContainer container, HttpServletResponse response) {
		
		if (container == null || StringUtil.isEmpty(container.getUuid()) || container.getUser() == null || container.getUser().getUserId() <= 0) {
			return ;
		}
		
		long now = System.currentTimeMillis();
		if (now - container.getTimestamp() <= Constants.USER_CACHE_DB_REFRESH) {
			return ;
		}
		
		response.addCookie(CookieFactory.getUserCookie(new UserCookieContainer(container.getUuid(), container.getUser(), now)));
	}
	
	private void updateBackUserStatus(BackUserCookieContainer backContainer, BackUserInfo backUser, HttpServletResponse response) {
		if (backContainer == null) return;
		
		long now = System.currentTimeMillis();
		if (now - backContainer.getTimestamp() <= Constants.USER_CACHE_DB_REFRESH) {
			return ;
		}
		
		response.addCookie(CookieFactory.getBackUserCookie(new BackUserCookieContainer(backContainer.getUuid(), System.currentTimeMillis())));
		backUserCache.set(backContainer.getUuid(), backUser);
	}

}
