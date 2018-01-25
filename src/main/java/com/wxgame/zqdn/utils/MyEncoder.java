package com.wxgame.zqdn.utils;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class MyEncoder {
	
	private static final Logger logger = LoggerFactory.getLogger(MyEncoder.class);
	
	public static String md5EncodeStr(String s) {
		
        try {
        	MessageDigest md5 = MessageDigest.getInstance("MD5");
        	md5.update(s.getBytes());
			byte[] b=md5.digest();
			int i;
            StringBuffer buf = new StringBuffer("");
            int size = b.length;
            for (int offset = 0; offset < size; offset++) {
                 i = b[offset];
                 if (i < 0)
                     i += 256;
                 if (i < 16)
                     buf.append("0");
                 buf.append(Integer.toHexString(i));
            }
			return buf.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} 
		
	}
	
	public static String md5EncodeObjs(Object... objs){
		
		if(objs == null || objs.length == 0){
			return null;
		}
		
		StringBuffer str = new StringBuffer();
		for(Object obj : objs){
			
			if(obj == null) continue;
			str.append(JSON.toJSONString(obj));
		}
		
		return md5EncodeStr(str.toString());
	}

}

