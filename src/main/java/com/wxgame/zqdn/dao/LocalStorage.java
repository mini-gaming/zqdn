package com.wxgame.zqdn.dao;


import java.util.concurrent.ConcurrentHashMap;


import org.springframework.stereotype.Repository;


@Repository("localStorage")
public class LocalStorage {
	
	
	private ConcurrentHashMap<String,String> cache = new ConcurrentHashMap<String,String>();
	
	public void put(String key, String val){
		
		this.cache.put(key, val);
	}
	
	public String get(String key){
		return this.cache.get(key);
	}
	

}

