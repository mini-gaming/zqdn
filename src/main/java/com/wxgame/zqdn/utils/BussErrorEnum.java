package com.wxgame.zqdn.utils;

public enum BussErrorEnum {
	
	WEIXIN_API_ERR("B_101", "Call weixin api with error"),
	
	INVALID_REQUEST_PARAMS("B_102", "Invalid reqeuest parameters");
	
	private String code;
	
	private String msg;
	
	private BussErrorEnum(String code, String msg){
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
	

}
