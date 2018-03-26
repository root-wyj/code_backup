package com.shixi.web.common;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 用来路由选择使用哪个datasource
 * @author wuyingjie
 * @date 2017年5月18日
 */

public class DynamicDataSource extends AbstractRoutingDataSource{

	@Override
	protected Object determineCurrentLookupKey() {
		String dataSource = DataSourceContextHolder.getDataSourceType();
//		System.out.println("========获取数据源：" + dataSource);
		return dataSource;
	}

}
