package com.wxgame.zqdn.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.wxgame.zqdn.dao.LocalStorage;

@Service("initializeService")
public class InitializeService implements ApplicationListener<ContextRefreshedEvent>{
	
	@Autowired
	private LocalStorage localStorage;
	
	@Autowired
	private GameInfoService gameInfoService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		if(event.getApplicationContext().getParent() == null){
			
			List<Map<String, Object>> ret = gameInfoService.getAllMaxScoreMap(1);
			localStorage.initialMaxScoreMap(ret);
			
			List<Map<String, Object>> ret1 = gameInfoService.getKingScoreMap();
			localStorage.initialKingScore(ret1);
		}
		
	}

}
