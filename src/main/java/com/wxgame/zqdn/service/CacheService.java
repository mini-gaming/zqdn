package com.wxgame.zqdn.service;

public interface CacheService {
	
	public void setCache(String key, String value);
	
	public String getCache(String key);

}
