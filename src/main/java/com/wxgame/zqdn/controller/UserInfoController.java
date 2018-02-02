package com.wxgame.zqdn.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.wxgame.zqdn.model.BasicHttpResponse;
import com.wxgame.zqdn.service.UserInfoService;

@Controller
@RequestMapping("/user")
public class UserInfoController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);
	
	@Autowired
	private UserInfoService userInfoSerivce;
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public BasicHttpResponse register(Model model, @RequestBody Map<String,Object> data){
		
		logger.info("User Register:\t"+JSON.toJSONString(data));
		return userInfoSerivce.registerNewUser(data);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public BasicHttpResponse login(Model model, @RequestBody Map<String,Object> data){
		
		return userInfoSerivce.login(data);
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@ResponseBody
	public BasicHttpResponse logout(Model model, @RequestBody Map<String,Object> data){
		
		return userInfoSerivce.logout(data);
	}

}
