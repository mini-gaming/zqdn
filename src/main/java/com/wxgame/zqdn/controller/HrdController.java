package com.wxgame.zqdn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hrd")
public class HrdController {
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	@ResponseBody
	public String index(Model model){
		
		return "Welcome to hrd!!!";
	}

}
