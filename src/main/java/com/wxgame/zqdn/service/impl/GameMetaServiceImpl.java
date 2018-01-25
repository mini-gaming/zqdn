package com.wxgame.zqdn.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.wxgame.zqdn.dao.CommonDao;
import com.wxgame.zqdn.service.GameMetaService;
import com.wxgame.zqdn.utils.CheckCache;

@Service("gameMetaService")
public class GameMetaServiceImpl implements GameMetaService {
	
	@Autowired
	private CommonDao commonDao;

	@Override
	public void insert() {
		
		String sql = "insert into zqdn_game_lkp (GAME_ID,GAME_NAME) values (:gameId,:gameName)";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("gameId", 1);
		params.put("gameName", "一眼识人");
		commonDao.insert(sql, params);
	}

	@CheckCache(key="gamename")
	@Override
	public String queryByGameName(String gamename) {
		
		List<Map<String, Object>> ret = commonDao.queryForList("select * from zqdn_game_lkp where GAME_NAME='"+gamename+"'");
		return JSON.toJSONString(ret);

	}

}
