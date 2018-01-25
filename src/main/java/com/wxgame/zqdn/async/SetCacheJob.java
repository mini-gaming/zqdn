package com.wxgame.zqdn.async;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wxgame.zqdn.service.CacheService;



public class SetCacheJob implements Callable<Boolean> {
	
	private static final Logger logger = LoggerFactory.getLogger(SetCacheJob.class);
	
	
	private String key;
	
	private String value;
	
	private CacheService cacheService;
	
	public SetCacheJob(CacheService cacheService, String key, String value){
		this.key = key;
		this.value = value;
		this.cacheService = cacheService;
	}

	@Override
	public Boolean call() throws Exception {
		
		try {
			cacheService.setCache(key, value);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

}