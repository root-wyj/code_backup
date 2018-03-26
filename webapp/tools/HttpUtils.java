package com.shixi.web.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shixi.web.common.Constants;

/**
 * 发送http请求类
 * @Description: TODO
 * @author heyong
 * @date 2017年3月9日
 */
public class HttpUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	private static int SOCKET_TIMEOUT = 5000;
	private static int CONNECT_TIMEOUT = 5000;
	private static int CONNECTION_REQUEST_TIMEOUT = 1000;
	
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
	    if (null != header && header.equalsIgnoreCase("XMLHttpRequest")) {
	    	return true;
	    } else {
	    	return false;
	    }
	}

	public static JSONObject doPost(String url, JSONObject jsonData) {
		return HttpUtils.doPost(url, jsonData, null);
	}
	
	public static JSONObject doPost(String url, JSONObject jsonData,  List<Cookie> cookies) {
		JSONObject jsonResult = null;
		CloseableHttpClient httpClient;
		
		if (null != cookies && cookies.size() > 0) {
			CookieStore cookieStore = new BasicCookieStore();	        
	        for (Cookie c : cookies) {
	        	cookieStore.addCookie(c);
	        }
			httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		} else {
			httpClient = HttpClients.createDefault();
		}
		
		CloseableHttpResponse response = null;
		try {
			HttpPost method = new HttpPost(url);
			RequestConfig config = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).build();
			method.setConfig(config);
			
			if (null != jsonData) {
				// 解决中文乱码问题
				StringEntity entity = new StringEntity(jsonData.toString(), "utf-8");
				entity.setContentEncoding("UTF-8");
				entity.setContentType("application/json");
				method.setEntity(entity);
			}
			HttpUtils.addHeaders(method);
			logger.debug("后台 POST请求:{} 请求参数：{}", url, jsonData);
			response = httpClient.execute(method);
			
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					String str = EntityUtils.toString(entity);
					if (!StringUtils.isEmpty(str)) {
						jsonResult = JSON.parseObject(str);
					}
					EntityUtils.consume(entity);
				}
			} else {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					String str = EntityUtils.toString(entity);
					if (!StringUtils.isEmpty(str)) {
						jsonResult = JSON.parseObject(str);
					}
					EntityUtils.consume(entity);
				}
			}
		} catch (Exception e) {
			logger.error("后台 POST请求错误:" + url, e);
		} finally {
            try {
                if (null != response) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		
		logger.debug("后台 POST响应:{} 返回结果：{}", url, jsonResult);
		return jsonResult;
	}

	public static JSONObject doGet(String url) {
		return HttpUtils.doGet(url, null);
	}
	
	public static JSONObject doGet(String url, Map<String, String>params) {
		return HttpUtils.doGet(url, params, null);
	}
	
	public static JSONObject doGet(String url, Map<String, String>params, List<Cookie> cookies) {
		JSONObject jsonResult = null;
		CloseableHttpClient httpClient;
		
		if (null != cookies && cookies.size() > 0) {
			CookieStore cookieStore = new BasicCookieStore();	        
	        for (Cookie c : cookies) {
	        	cookieStore.addCookie(c);
	        }
			httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		} else {
			httpClient = HttpClients.createDefault();
		}
		
		CloseableHttpResponse response = null;
		
		try {
			URIBuilder builder = new URIBuilder(url);
			if (null != params) {
				for (String key : params.keySet()) {
					builder.addParameter(key, params.get(key));
				}
			}
			URI uri = builder.build();
			logger.debug("后台 GET请求:{}", uri.toString());
			HttpGet request = new HttpGet(uri);
			RequestConfig config = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).build();
			request.setConfig(config);
			
			response = httpClient.execute(request);
			
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					String str = EntityUtils.toString(entity);
					if (!StringUtils.isEmpty(str)) {
						jsonResult = JSON.parseObject(str);
					}
					EntityUtils.consume(entity);
				}
			} else {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					String str = EntityUtils.toString(entity);
					if (!StringUtils.isEmpty(str)) {
						jsonResult = JSON.parseObject(str);
					}
					EntityUtils.consume(entity);
				}
			}
		} catch (Exception e) {
			logger.error("后台 GET请求错误:" + url, e);
		} finally {
            try {
                if (null != response) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		
		logger.debug("后台 GET响应:{} 返回结果：{}", url, jsonResult);
		return jsonResult;
	}
	
	/**
	 * 
	 * @param url
	 * @param params
	 * @author wyj
	 */
	public static byte[] doRawGet(String url, Map<String, String> params) {
		byte[] result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		
		try {
			URIBuilder builder = new URIBuilder(url);
			if (null != params) {
				for (String key : params.keySet()) {
					builder.addParameter(key, params.get(key));
				}
			}
			URI uri = builder.build();
			logger.debug("GET请求: " + uri.toString());
			HttpGet request = new HttpGet(uri);
			response = httpClient.execute(request);
			
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					result = EntityUtils.toByteArray(entity);
					EntityUtils.consume(entity);
				}
			} else {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					result = EntityUtils.toByteArray(entity);
					EntityUtils.consume(entity);
				}
			}
			logger.debug("GET状态码：" + response.getStatusLine().getStatusCode());
		} catch (IllegalArgumentException e) {
			logger.error("无效的url: " + url, e);
		} catch (Exception e) {
			logger.error("get请求提交失败:" + url,  e);
		} finally {
            try {
                if (null != response) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param url
	 * @param params
	 * @author wyj
	 */
	public static byte[] doRawPost(String url, Map<String, String> params) {
		byte[] result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		
		try {
			URIBuilder builder = new URIBuilder(url);
			if (null != params) {
				for (String key : params.keySet()) {
					builder.addParameter(key, params.get(key));
				}
			}
			URI uri = builder.build();
			logger.debug("POST请求: " + uri.toString());
			HttpPost request = new HttpPost(uri);
			response = httpClient.execute(request);
			
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					result = EntityUtils.toByteArray(entity);
					EntityUtils.consume(entity);
				}
			} else {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					result = EntityUtils.toByteArray(entity);
					EntityUtils.consume(entity);
				}
			}
			logger.debug("GET状态码：" + response.getStatusLine().getStatusCode());
			response.close();
		} catch (IllegalArgumentException e) {
			logger.error("无效的url: " + url, e);
		} catch (Exception e) {
			logger.error("get请求提交失败:" + url,  e);
		} finally {
            try {
                if (null != response) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		
		return result;
	}
	
	public static String parseStringFromRequestStream(HttpServletRequest request) {
		InputStream is = null;
		String contentStr = null;
		
		try {
			is = request.getInputStream();
			contentStr = IOUtils.toString(is, Constants.UTF8);
		} catch (IOException e) {
			logger.error("获取参数失败", e);
		}
		
		logger.debug("前端 " + request.getMethod() + "请求：" + request.getRequestURL() + " 参数：" + contentStr);
		
		return contentStr;
	}
	
	public static JSONObject parseStreamToJSONObject(HttpServletRequest request) {
		JSONObject obj = null;

		try {
			InputStream is = request.getInputStream();
			String contentStr = IOUtils.toString(is, Constants.UTF8);
			if (!StringUtils.isEmpty(contentStr)) {
				obj = JSON.parseObject(contentStr);
			}
		} catch (Exception e) {
			logger.error("解析请求参数失败，", e);
		}
		
		if (null == obj) {
			obj = new JSONObject();
		}

		logger.debug("前端{}请求：{} 参数：{}", request.getMethod(), request.getRequestURL(), obj.toString());
		
		return obj;
	}
	
	public static String parseStreamToJSONString(HttpServletRequest request) {
		String content = "";
		
		try {
			InputStream is = request.getInputStream();
			content = IOUtils.toString(is, Constants.UTF8);
		} catch (Exception e) {
			logger.error("解析请求参数失败，", e);
		}
		
		logger.debug("前端{}请求：{} 参数：{}", request.getMethod(), request.getRequestURL(), content);
		
		return content;
	}
	
	public static String parseParamsToString(HttpServletRequest request) {
		return HttpUtils.parseParamsToJSONObject(request).toString();
	}
	
	public static JSONObject parseParamsToJSONObject(HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		
		@SuppressWarnings("unchecked")
		Enumeration<String> paramNames = request.getParameterNames();
		
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				if (paramValue.length() != 0) {
					obj.put(paramName, paramValue);
				}
			}
		}
		
		return obj;
	}
	
    /**
     * 添加请求头
     * @param entity
     */
    private static void addHeaders(HttpRequestBase entity) {
//        entity.addHeader("Accept", "application/json");
        entity.addHeader("Content-Type", "application/json;charset=UTF-8");
    }
}
