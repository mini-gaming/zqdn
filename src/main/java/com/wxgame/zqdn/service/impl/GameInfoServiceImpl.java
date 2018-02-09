package com.wxgame.zqdn.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.dao.CommonDao;
import com.wxgame.zqdn.dao.LocalStorage;
import com.wxgame.zqdn.model.BasicHttpResponse;
import com.wxgame.zqdn.service.GameInfoService;
import com.wxgame.zqdn.utils.PropUtils;
import static com.wxgame.zqdn.utils.SysErrorEnum.DB_INSERT_ERR;;

@Service("gameInfoService")
public class GameInfoServiceImpl implements GameInfoService {
	
	private static final Logger logger = LoggerFactory
			.getLogger(GameInfoServiceImpl.class);
	
	@Autowired
	private CommonDao commonDao;
	
	@Autowired
	private LocalStorage localStorage;

	@Override
	public BasicHttpResponse recordGameInstance(Map<String,Object> data) {
		
		String sql = PropUtils.getSql("GameInfoService.recordGameInstance");
		try {
			commonDao.insert(sql, data);
			int gameId = (int) data.get("gameId");
			int personalMax = (int) data.get("personalMax");
			int gameMax = localStorage.getKingScore(gameId);
			int currScore = (int) data.get("score");
			if(currScore>personalMax){
				updatePersonalMaxScore(data);
				
			}
			if(currScore>gameMax){
				updateGameMaxScore(data);
				
			}
			return BasicHttpResponse.success();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return BasicHttpResponse.error(DB_INSERT_ERR.getCode(), DB_INSERT_ERR.getMsg());
		}
		
	}
	
	@Override
	public int queryPersonalMaxScore(Map<String,Object> data){
		
		String sql = PropUtils.getSql("GameInfoService.queryMaxScore");
		int score = commonDao.queryForInt(sql,data);
		if(score == 0){
			score = Integer.MIN_VALUE;
		}
		return score;
	}
	
	private int updatePersonalMaxScore(Map<String,Object> data){
		
		String sql = PropUtils.getSql("GameInfoService.updatePersonalMaxScore");
		return commonDao.update(sql, data);
	}
	
	private int updateGameMaxScore(Map<String,Object> data){
		
		String sql = PropUtils.getSql("GameInfoService.updateGameMaxScore");
		return commonDao.update(sql, data);
	}
	

	@Override
	public JSONObject getGameRank(Map<String, Object> data) {
		
		data.put("openId", data.get("userId"));
		String sql = PropUtils.getSql("GameInfoService.getGameRank");
		List<Map<String,Object>> ret = commonDao.queryForList(sql, data);
		JSONObject j = new JSONObject();
		j.put("globalRank", ret.get(0).get("cnt"));
		j.put("globalUserCnt", ret.get(1).get("cnt"));
		j.put("friendsRank", ret.get(2).get("cnt"));
		j.put("friendsCnt", ret.get(3).get("cnt"));
		j.put("kingScore", ret.get(4).get("cnt"));
		return j;
	}
	
	

	@Override
	public JSONObject getGameRankWithCache(Map<String, Object> data) {
		

		int score = (int) data.get("score");
		int gameId = (int) data.get("gameId");
		int personalMax = (int) data.get("personalMax");
		JSONObject j = new JSONObject();
		
		int globalUserCnt = localStorage.getGlobalUserCnt(gameId);
		int globalRank = localStorage.getGlobalRank(gameId, score);
		int kingScore = localStorage.getKingScore(gameId);
		if(score < personalMax){
			globalRank--;
		}else{
			localStorage.updateMaxScoreMap(gameId, personalMax, score);
			personalMax = score;
			
		}
		
		if(score > kingScore){
			localStorage.updateKingScore(gameId, score);
			kingScore = score;
		}
			
		j.put("globalRank", globalRank);
		j.put("globalUserCnt", globalUserCnt);
		j.put("friendsRank", 1);
		j.put("friendsCnt", 1);
		j.put("kingScore", -kingScore);
		j.put("personalMaxScore", -personalMax);
		j.put("beatPercent", (globalUserCnt - globalRank + 1)*1.0f/globalUserCnt);
		return j;
	}

	@Override
	public List<Map<String, Object>> getAllMaxScoreMap(int gameId) {
		
		String sql = PropUtils.getSql("GameInfoService.getAllMaxScoreMap");
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("gameId", gameId);
		List<Map<String, Object>> ret =  commonDao.queryForList(sql, params);

		return ret;
	}

	@Override
	public List<Map<String, Object>> getKingScoreMap() {
		
		String sql = PropUtils.getSql("GameInfoService.getKingScoreMap");
		return commonDao.queryForList(sql);
	}

	

}
