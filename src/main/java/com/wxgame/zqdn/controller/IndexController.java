/**
 * 
 */
package com.wxgame.zqdn.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wxgame.zqdn.service.GameMetaService;



/**
 * @author fozhang
 *
 */
@Controller
public class IndexController {
	
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	private GameMetaService gameMetaService;
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public String index(Model model){
		
		logger.info("Welcome to zqdn!");
		return "Welcome to zqdn!!!";
	}
	
	@RequestMapping(value = "/insert", method = RequestMethod.GET)
	@ResponseBody
	public String insert(Model model){
		
		gameMetaService.insert();
		return "Insert success";
	}
	
	@RequestMapping(value = "/game", method = RequestMethod.GET)
	@ResponseBody
	public String game(Model model, String name){
		
		return gameMetaService.queryByGameName(name);
	}
	
	
	
}
