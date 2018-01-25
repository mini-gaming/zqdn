package com.wxgame.zqdn.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wxgame.zqdn.dao.LocalStorage;
import com.wxgame.zqdn.service.CacheService;

@Service("cacheService")
public class CacheServiceImpl implements CacheService {
	
	@Autowired
	private LocalStorage localStorage;

	@Override
	public void setCache(String key, String value) {
		
		localStorage.put(key, value);
	}

	@Override
	public String getCache(String key) {
		
		return localStorage.get(key);
	}

}
