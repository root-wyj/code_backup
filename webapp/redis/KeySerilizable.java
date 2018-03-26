package com.shixi.web.authorize.redis;

/**
 * 
 * @author wuyingjie
 * @date 2017年10月20日
 */

public interface KeySerilizable<K> {
	String serilizableKey(K key);
}
