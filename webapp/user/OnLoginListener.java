package com.shixi.web.listener;

import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shixi.web.authorize.cookie.CookieFactory;
import com.shixi.web.authorize.cookie.UserCookieContainer;
import com.shixi.web.authorize.redis.RedisCacheIoc;
import com.shixi.web.authorize.redis.UserRedisCacheManager;
import com.shixi.web.authorize.redis.model.AUUserCacheKeyModel;
import com.shixi.web.common.RMV;
import com.shixi.web.exception.ServiceException;
import com.shixi.web.model.AUUser;
import com.shixi.web.service.UserService;

/**
 * 
 * @author wuyingjie
 * @date 2017年12月7日
 */

@Component
public class OnLoginListener {

	private static final Logger logger = LoggerFactory.getLogger(OnLoginListener.class);
	
	@Resource(name=RedisCacheIoc.USER_CACHE)
	UserRedisCacheManager userCache;
	@Autowired
	UserService userService;
	
	public void loginedIn(HttpServletRequest request, HttpServletResponse response, long userId) {
		AUUser user = userService.getAUUserSimple(userId);
		if (user == null || user.getUserId() <= 0) {
			logger.error("更新为登录状态时，发现数据不一致，并不能找到userId={}的用户", user);
			throw new ServiceException("数据不一致性错误！");
		}
		
		//更新cookie
		String uuid = UUID.randomUUID().toString();
		response.addCookie(CookieFactory.getUserCookie(new UserCookieContainer(uuid, user, System.currentTimeMillis())));
		//添加到cache中
		userCache.set(new AUUserCacheKeyModel(user.getUserId(), uuid), user);
		response.addCookie(CookieFactory.getNativeIdCookie(user.getUserId()));
		
		
		
		try {
			request.getRequestDispatcher("/api"+RMV.USER_INFO+"?id="+user.getUserId()).forward(request, response);
		} catch (Exception e) {
			logger.error("登录 requestDispatcher出错", e);
		}
	}
	
}
