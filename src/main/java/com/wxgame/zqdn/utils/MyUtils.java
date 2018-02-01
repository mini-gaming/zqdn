package com.wxgame.zqdn.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;

public class MyUtils {
	
	public static Map<String,Object> toMap(JSONObject params){
		
		Assert.notNull(params);
		Assert.notEmpty(params);
		Map<String,Object> m = new HashMap<String,Object>(params.size());
		Iterator<String> iter = params.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			m.put(key, params.get(key));
		}
		return m;
		
	}

}