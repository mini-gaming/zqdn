package com.wxgame.zqdn.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.wxgame.zqdn.service.CronService;

@Service("cronService")
public class CronServiceImpl implements CronService {

	@Scheduled(cron="0/5 * *  * * ? ")
	@Override
	public void cacheAllUserMaxScore() {
		

	}

}
