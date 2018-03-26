package com.shixi.web.common;

/**
 * 
 * @author wuyingjie
 * @date 2017年5月18日
 */

public class DataSourceContextHolder {	
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
	
	public static void setDataSourceType(String type) {
		contextHolder.set(type);
	}
	
	public static String getDataSourceType() {
		return contextHolder.get();
	}
	
	public static void clearDataSourceType() {
		contextHolder.remove();
	}
	
}
