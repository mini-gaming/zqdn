package com.wxgame.zqdn.model;

import com.alibaba.fastjson.JSONObject;

public class BasicHttpResponse {
	
	private boolean success;
	
	private String errCode;
	
	private String errMsg;
	
	private JSONObject data;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}
	
	public static BasicHttpResponse success(){
		
		BasicHttpResponse res = new BasicHttpResponse();
		res.success = true;		
		return res;
	}
	
	public static BasicHttpResponse successResult(JSONObject data){
		
		BasicHttpResponse res = new BasicHttpResponse();
		res.success = true;		
		res.setData(data);
		return res;
	}
	
	public static BasicHttpResponse error(String code, String msg){
		
		BasicHttpResponse res = new BasicHttpResponse();
		res.success = false;
		res.setErrCode(code);
		res.setErrMsg(msg);
		return res;
	}

}
