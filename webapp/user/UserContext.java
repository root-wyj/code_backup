package com.shixi.web.authorize;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shixi.web.common.Constants;
import com.shixi.web.exception.UserPermissionException;
import com.shixi.web.model.AUUser;

/**
 * 系统中 获取当前用户的类
 * 
 * @author wuyingjie
 * @date 2017年3月29日
 */
public class UserContext {
	static Logger logger = LoggerFactory.getLogger(UserContext.class);
	
	static final ThreadLocal<Map<String, Object>> current = new ThreadLocal<>();
	
	public UserContext(AUUser user, String uuid) {
		Map<String, Object> map = new HashMap<>();
		map.put("user", user);
		map.put("uuid", uuid);
		current.set(map);
		
//		if (user != null) {
//			MDC.put("user_id", user.getUserId() + "");
//		} else {
//			
//		}
	}
	
	/**
	 * 获取 当前用户
	 */
	public static AUUser getCurrentUser() {
		Map<String, Object> map = current.get();
		AUUser user = null;
		if (map != null) {
			user = (AUUser)map.get("user");
		}
		return user;
	}
	
	public static long getCurrentUserId() {
		AUUser user = getCurrentUser();
		
		if (user != null) {
			return user.getUserId();
		}
		
		return 0;
	}
	
	public static boolean isCurrentTestUser() {
		long id = getCurrentUserId();
		if (id > 0 && id < Constants.TEST_USER_MAX_ID) {
			return true;
		} else {
			return false;
		}
	}
	
	public static long getRequiredCurrentUserId() {
		AUUser user = UserContext.getCurrentUser();
		
		if (user != null && user.getUserId() > 0) {
			return user.getUserId();
		} else {
			throw new UserPermissionException();
		}
	}
	
	public static String getUUid() {
		Map<String, Object> map = current.get();
		if (map != null) {
			return (String)map.get("uuid");
		}
		return "";
	}
	
	public void close() {
		current.remove();
//		MDC.remove("user_id");
	}
}
