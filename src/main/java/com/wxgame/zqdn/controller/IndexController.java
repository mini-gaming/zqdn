package com.wxgame.zqdn.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public String welcome(Model model){
		
		return "Welcome to ZQDN! 谢谢";
		
	}

}
