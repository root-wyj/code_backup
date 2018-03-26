package com.shixi.web.authorize.redis.model;

/**
 * 
 * @author wuyingjie
 * @date 2017年11月1日
 */

public class AUUserCacheKeyModel {
	private long userId;
	private String uuid;
	
	public AUUserCacheKeyModel() {}
	
	public AUUserCacheKeyModel(long userId, String uuid) {
		this.userId = userId;
		this.uuid = uuid;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
