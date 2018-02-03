package com.wxgame.zqdn.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.model.BasicHttpResponse;

public interface UserInfoService {
	
	
	public BasicHttpResponse registerNewUser(Map<String, Object> data);

	public BasicHttpResponse login(Map<String, Object> data);

	public BasicHttpResponse logout(Map<String, Object> data);
	
	public JSONObject getUserInfoByGame(Map<String, Object> data);
	
	public JSONObject codeToOpenId(String code);

}
