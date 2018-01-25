package com.wxgame.zqdn.utils;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.wxgame.zqdn.async.SetCacheJob;
import com.wxgame.zqdn.service.CacheService;



public class CacheAspect {
	
	private static final Logger logger = LoggerFactory
			.getLogger(CacheAspect.class);
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	
	
	@SuppressWarnings("unchecked")
	public Object around(ProceedingJoinPoint point) throws Throwable {
        
		Method m = getMethodByJoinPoint(point);
		CheckCache checkCacheAnn = m.getAnnotation(CheckCache.class);
		Object[] args = point.getArgs();
		
		if(!"true".equalsIgnoreCase(PropUtils.getServiceConfig("enableCache"))){
			return point.proceed(args);
		}
		String fullKey = resolveKey(checkCacheAnn.key(),args);
		
		String result = null;
		try {
			result = cacheService.getCache(fullKey);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if(!StringUtils.isEmpty(result) && !"null".equalsIgnoreCase(result)){
			
			@SuppressWarnings("rawtypes")
			Class returnClazz = m.getReturnType();
			Object resultObject = JSON.parseObject(result, returnClazz);
			return resultObject;
		}
		Object dbResult = point.proceed(args);
		if(dbResult != null){
			taskExecutor.submit(new SetCacheJob(cacheService, fullKey, JSON.toJSONString(dbResult)));
		}
        return dbResult; 
    }
	
	private String resolveKey(String rawKey, Object[] args){
		
		if(StringUtils.isEmpty(rawKey)){
			return "";
			
		}
		if(args == null || args.length == 0){
			return rawKey;
		}
		int size = args.length;
		Object[] _args = new String[size];
		for(int i=0; i<size; i++){
			_args[i] = JSON.toJSONString(args[i]);
		}
		String fullKey = MessageFormat.format(rawKey, _args);
		return MyEncoder.md5EncodeStr(fullKey);
	}
	
	private Method getMethodByJoinPoint(ProceedingJoinPoint point) throws Exception{
		
		Signature sig = point.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException();
        }
        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        return currentMethod;
	}

}