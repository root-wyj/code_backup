package com.shixi.web.authorize.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shixi.web.authorize.redis.model.AUUserCacheKeyModel;
import com.shixi.web.authorize.redis.model.VerifyCacheKeyModel;
import com.shixi.web.common.Constants;
import com.shixi.web.model.user.BackUserInfo;

/**
 * 
 * @author wuyingjie
 * @date 2017年10月25日
 */
@Configuration
public class RedisCacheIoc {
	
//	@Bean(name="loginTimeCache")
//	public LongRedisCacheManager<User> loginTimeCM() {
//		return new LongRedisCacheManager<User>(RedisCacheManager.DEFAULT_TIMEOUT, new KeySerilizable<User>() {
//			@Override
//			public String serilizableKey(User key) {
//				return "loginTimeCache_"+key.getClass().getName()+"_"+key.getId();
//			}
//		});
//	}
	
//	@Bean(name="userCache")
//	public RedisCacheManager<String, User> userCM() {
//		return new RedisCacheManager<String, User>(20*60, new KeySerilizable<String>() {
//			@Override
//			public String serilizableKey(String key) {
//				return "userCache_"+key.getClass().getName()+"_"+key;
//			}
//		});
//	}
	
	@Bean
	public Jackson2JsonRedisSerializer<? extends Object> jackson2JsonRedisSerializer() {
		Jackson2JsonRedisSerializer<? extends Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
	}
	
	public static final String MOBILE_CODE_DURING_CACHE = "mobileCodeDuringCache";
	
	//记录 发送短信间隔的，1分钟只能发送一条
	@Bean(name=MOBILE_CODE_DURING_CACHE)
	public RedisCacheManager<String, String> mobileCodeDuringCM() {
		return new RedisCacheManager<String, String>(Constants.MOBILE_MSG_GET_DURING, new KeySerilizable<String>() {
			@Override
			public String serilizableKey(String key) {
				return MOBILE_CODE_DURING_CACHE+"_"+key;
			}
		});
	}
	
	public static final String MOBILE_CODE_TIMES_CACHE = "mobileCodeTimesCache";
	
	//记录发送短信条数的，限制单手机发送条数
	@Bean(name=MOBILE_CODE_TIMES_CACHE)
	public LongRedisCacheManager<String> mobileCodeTimesCM() {
		return new LongRedisCacheManager<String>(60*60*24, new KeySerilizable<String>() {
			@Override
			public String serilizableKey(String key) {
				return MOBILE_CODE_TIMES_CACHE+"_"+key;
			}
		});
	}
	
	public static final String PSWD_ERROR_TIMES_CACHE = "pswdErrorTimesCache";
	
	//记录密码输入错误次数
	@Bean(name=PSWD_ERROR_TIMES_CACHE)
	public LongRedisCacheManager<Long> pswdErrorTimesCM() {
		return new LongRedisCacheManager<Long>(60*30, new KeySerilizable<Long>() {
			@Override
			public String serilizableKey(Long key) {
				return PSWD_ERROR_TIMES_CACHE+"_"+key;
			}
		});
	}
	
	public static final String VALID_ICON_CACHE = "validIconCache";
	
	//记录验证码的，用于验证验证码是否输入正确
	@Bean(name=VALID_ICON_CACHE)
	public RedisCacheManager<String, String> validIconCache() {
		return new RedisCacheManager<>(Constants.COOKIE_VALID_ICON_MAX_LIVE, new KeySerilizable<String>() {
			@Override
			public String serilizableKey(String key) {
				return VALID_ICON_CACHE+"_"+key;
			}
		});
	}
	
	
	public static final String MSG_CODE_CACHE = "msgCodeCache";
	
	//记录短信或者email发送的验证码
	@Bean(name=MSG_CODE_CACHE)
	public RedisCacheManager<String, String> msgCodeCache() {
		return new RedisCacheManager<>(Constants.PHONE_CODE_EXPIRED, new KeySerilizable<String>() {
			@Override
			public String serilizableKey(String key) {
				return MSG_CODE_CACHE+"_"+key;
			}
		});
	}
	
	
	public static final String VERIFY_TOKEN_CACHE = "verifyTokenCache";
	
	//记录 身份验证部分需要缓存的验证信息
	@Bean(name=VERIFY_TOKEN_CACHE)
	public RedisCacheManager<VerifyCacheKeyModel, String> verifyCache() {
		return new RedisCacheManager<>(Constants.VALIDATE_TOKEN_EXPIRED, new KeySerilizable<VerifyCacheKeyModel>() {
			@Override
			public String serilizableKey(VerifyCacheKeyModel key) {
				return VERIFY_TOKEN_CACHE+"_"+key.toString();
			}
		});
	}
	
	
	public static final String USER_CACHE = "userCache";
	
	@Bean(name=USER_CACHE)
	public UserRedisCacheManager userCache() {
		return new UserRedisCacheManager(Constants.COOKIE_USER_EXPIRED, new KeySerilizable<AUUserCacheKeyModel>() {
			@Override
			public String serilizableKey(AUUserCacheKeyModel key) {
				return USER_CACHE+key.getUserId()+"_"+key.getUuid();
			}
		});
	}
	
	public static final String BACK_USER_CACHE = "backUserCache";
	
	@Bean(name=BACK_USER_CACHE)
	public RedisCacheManager<String, BackUserInfo> backUserCache() {
		return new RedisCacheManager<>(Constants.COOKIE_BACK_USER_EXPIRED, new KeySerilizable<String>() {
			@Override
			public String serilizableKey(String key) {
				return BACK_USER_CACHE+"_"+key;
			}
		});
	}
}
