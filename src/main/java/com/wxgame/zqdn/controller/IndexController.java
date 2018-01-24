/**
 * 
 */
package com.wxgame.zqdn.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * @author fozhang
 *
 */
@Controller
public class IndexController {
	
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public String index(Model model){
		
		logger.info("Welcome to zqdn!");
		return "Welcome to zqdn!!!";
	}
	
	
	
}
