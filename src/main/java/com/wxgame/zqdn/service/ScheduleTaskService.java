package com.wxgame.zqdn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduleTaskService {
	
	@Autowired
	private GameInfoService gameInfoService;
	
	@Scheduled(cron = "0 0 2 * * ?")
	public void updateIdioms(){
		
		gameInfoService.updateIdioms(0);
		
	}
	
	

}
