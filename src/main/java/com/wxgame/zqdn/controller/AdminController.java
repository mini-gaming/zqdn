package com.wxgame.zqdn.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.dao.LocalStorage;
import com.wxgame.zqdn.service.GameInfoService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private LocalStorage localStorage;
	
	@Autowired
	private GameInfoService gameInfoService;
	
	@RequestMapping(value = "/peekMaxScore", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject peekMaxScore(Model model){
		
		return localStorage.peekMaxScoreCache();
		
	}
	
	@RequestMapping(value = "/peekIdiom", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject peekIdiom(Model model){
		
		return localStorage.peekIdiomsCache();
		
	}
	
	@RequestMapping(value = "/analysisPlay", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject analysisPlay(Model model){
		
		return gameInfoService.analysisPlay();
		
	}
	
	@RequestMapping(value = "/analysisVisit", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject analysisVisit(Model model){
		
		return gameInfoService.analysisVisit();
		
	}

}
