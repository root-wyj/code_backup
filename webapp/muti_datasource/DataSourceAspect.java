package com.shixi.web.aop.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shixi.web.annotation.DataSource;
import com.shixi.web.common.DataSourceContextHolder;
import com.shixi.web.common.DataSourceKey;
import com.shixi.web.common.TransactionKey;

/**
 * @Description: 数据源Aspect组件
 * @author heyong
 * @date 2017年6月19日
 */
@Component
@Aspect

/**
 * 注意这里设置执行顺序Order必须比<tx:annotation-driven transaction-manager="txManager1" order="1"/>
 * 配置中的order事务执行顺序小才能在事务获取数据源之前切换数据源。
 */
@Order(0)
public class DataSourceAspect {
	
	@Pointcut("execution(* com.shixi.web.mapper.*.*(..))")
	public void mapperAspect() {}
	
	@Pointcut("execution(* com.shixi.web.service.*.*(..))")
	public void serviceAspect() {}
	
	@Pointcut("execution(* com.shixi.web.dao.*.*(..))")
	public void daoAspect() {}
	
	@Before("mapperAspect()")
	public void beforeMapper(JoinPoint joinPoint) throws Exception {		
		
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature)signature;  
		Method method = methodSignature.getMethod();
		
		String name = null;
		// 取得方法上的DataSource注解
		if (method.isAnnotationPresent(DataSource.class)) {
			DataSource annotation = method.getAnnotation(DataSource.class);
			name = annotation.value();
		} 
		// 取得类上的DataSource注解
		else if (method.getDeclaringClass().isAnnotationPresent(DataSource.class)) {
			DataSource annotation = method.getDeclaringClass().getAnnotation(DataSource.class);
			name = annotation.value();
		}
		
		if (null != name) {
			DataSourceContextHolder.setDataSourceType(name); 
//			System.out.println("========设置数据源：" + name);
		} else {
			// 没有DataSource注解，设置默认数据源
			DataSourceContextHolder.setDataSourceType(DataSourceKey.DATASOURCE_A); 
//			System.out.println("========设置数据源：" + DataSourceKey.DATASOURCE_A);
		}
		
		// 另一种方法不使用DynamicDataSource，直接修改SqlSessionFactoryBean和DataSourceTransactionManager中的dataSource属性，以后做分布式事务再考虑。
		/*
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		
		javax.sql.DataSource dataSource;
		if (null != name) {
			dataSource = (javax.sql.DataSource)wac.getBean(name); 
		} else {
			// 没有DataSource注解，设置默认数据源
			dataSource = (javax.sql.DataSource)wac.getBean(DataSourceKey.DATASOURCE_A);
		}
		
		// 修改数据源
		SqlSessionFactoryBean sqlSessionFactoryBean = (SqlSessionFactoryBean)wac.getBean(SqlSessionFactoryBean.class);
		Environment environment = sqlSessionFactoryBean.getObject().getConfiguration().getEnvironment();
		Field dataSourceField = environment.getClass().getDeclaredField("dataSource");
		
		// 指示反射的对象在使用时应该取消java语言访问检查
		dataSourceField.setAccessible(true);
		
		dataSourceField.set(environment, dataSource);
		*/
	}
	
	@Before("serviceAspect()")
	public void beforeService(JoinPoint joinPoint) throws Exception {	
		this.doSomething(joinPoint);
	}
	
	@Before("daoAspect()")
	public void beforeDao(JoinPoint joinPoint) throws Exception {
		this.doSomething(joinPoint);
	}
	
	private void doSomething(JoinPoint joinPoint) throws Exception {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature)signature;  
		Method method = methodSignature.getMethod();
		
		String name = null;
		// 取得方法上的Transactional注解
		if (method.isAnnotationPresent(Transactional.class)) {
			Transactional annotation = method.getAnnotation(Transactional.class);
			name = annotation.value();
		} 
		// 取得类上的Transactional注解
		else if (method.getDeclaringClass().isAnnotationPresent(Transactional.class)) {
			Transactional annotation = method.getDeclaringClass().getAnnotation(Transactional.class);
			name = annotation.value();
		} 
		else {
			return;
		}
		
		String dataSource;
		
		if (null != name) {
			if (name.equals(TransactionKey.TXMANAGER_A)) {
				dataSource = DataSourceKey.DATASOURCE_A;
			} else if (name.equals(TransactionKey.TXMANAGER_B)) {
				dataSource = DataSourceKey.DATASOURCE_B;
			} else {
				dataSource = DataSourceKey.DATASOURCE_A;
			}
		} else {
			// 没有DataSource注解，设置默认数据源
			dataSource = DataSourceKey.DATASOURCE_A;
		}
		
		DataSourceContextHolder.setDataSourceType(dataSource);
//		System.out.println("========设置数据源（事务）：" + dataSource);
	}
}
