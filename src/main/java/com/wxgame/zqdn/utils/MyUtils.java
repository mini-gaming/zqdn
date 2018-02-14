package com.wxgame.zqdn.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	
	public static int getWeekIndex(){
		
		Calendar cal = Calendar.getInstance(); 
        int week_index = cal.get(Calendar.DAY_OF_WEEK);  
		return week_index;
		
	}
	
	public List<byte[]> readFile(String filePath) throws IOException{
		
		
			FileInputStream fis = new FileInputStream(new File("timg.jpg"));
			List<byte[]> fileBytes = new ArrayList<byte[]>();
			byte[] cache = new byte[1024];  
		        
		        while(fis.read(cache) != -1){  
		        	fileBytes.addAll(Arrays.asList(cache));
		        }
		        
		        fis.close();
			return fileBytes;
		
	}
	
	public static void main(String[] args){
		
		System.out.println(getWeekIndex());
	}

}
