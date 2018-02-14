package com.wxgame.zqdn.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.wxgame.zqdn.dao.LocalStorage;

public class InitializeService implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private LocalStorage localStorage;

	@Autowired
	private GameInfoService gameInfoService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		// event.getApplicationContext().getParent()
		List<Integer> allGame = gameInfoService.getAllGameIds();
		for(int gameId : allGame){
			List<Map<String, Object>> ret = gameInfoService.getAllMaxScoreMap(gameId);
			localStorage.initialMaxScoreMap(gameId, ret);
		}

		List<Map<String, Object>> ret1 = gameInfoService.getKingScoreMap();
		localStorage.initialKingScore(ret1);
		
		//gameInfoService.updateIdioms();

	}

}
