package com.wxgame.zqdn.utils;

//System Error
public enum SysErrorEnum {
	
	DB_INSERT_ERR("S_101","Insert DB with Error");
	
	private String code;
	
	private String msg;
	
	private SysErrorEnum(String code, String msg){
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
