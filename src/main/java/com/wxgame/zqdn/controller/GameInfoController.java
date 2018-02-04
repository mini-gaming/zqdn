package com.wxgame.zqdn.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.model.BasicHttpResponse;
import com.wxgame.zqdn.service.GameInfoService;

@Controller
@RequestMapping("/game")
public class GameInfoController {
	
	private static final Logger logger = LoggerFactory.getLogger(GameInfoController.class);
	
	@Autowired
	private GameInfoService gameInfoService;
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	
	
	@RequestMapping(value = "/record", method = RequestMethod.POST)
	@ResponseBody
	public BasicHttpResponse record(Model model, @RequestBody final Map<String,Object> data){
		
		logger.info(JSON.toJSONString(data));
		JSONObject rank = gameInfoService.getGameRankWithCache(data);
		taskExecutor.execute(new Runnable(){

			@Override
			public void run() {
				gameInfoService.recordGameInstance(data);
				
			}}, 300000);
		return BasicHttpResponse.successResult(rank);
		
	}

}
