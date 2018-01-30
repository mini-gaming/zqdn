package com.wxgame.zqdn.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.model.BasicHttpResponse;

public interface GameInfoService {
	
	public BasicHttpResponse recordGameInstance(Map<String,Object> data);
	
	public JSONObject getGameRank(Map<String,Object> data);
	

}
