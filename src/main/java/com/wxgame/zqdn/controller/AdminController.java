package com.wxgame.zqdn.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.dao.LocalStorage;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private LocalStorage localStorage;
	
	@RequestMapping(value = "/cache", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject cache(Model model){
		
		return localStorage.toJSON();
		
	}

}
