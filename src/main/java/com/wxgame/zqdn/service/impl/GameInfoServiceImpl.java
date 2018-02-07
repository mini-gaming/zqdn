package com.wxgame.zqdn.service.impl;

import java.util.ArrayList;
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
			List<Integer> scores = queryMaxScore(data);
			int personalMax = scores.get(0);
			int gameMax = scores.get(1);
			int currScore = (int) data.get("score");
			if(currScore>personalMax){
				updatePersonalMaxScore(data);
				localStorage.updateMaxScoreMap(gameId, currScore, personalMax);
			}
			if(currScore>gameMax){
				updateGameMaxScore(data);
				localStorage.updateKingScore(1, currScore);
			}
			return BasicHttpResponse.success();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return BasicHttpResponse.error(DB_INSERT_ERR.getCode(), DB_INSERT_ERR.getMsg());
		}
		
	}
	
	private List<Integer> queryMaxScore(Map<String,Object> data){
		
		String sql = PropUtils.getSql("GameInfoService.queryMaxScore");
		List<Map<String,Object>> ret = commonDao.queryForList(sql,data);
		List<Integer> scores = new ArrayList<Integer>(2);
		scores.add((int)(ret.get(0).get("MAX_SCORE")));
		scores.add((int)(ret.get(1).get("MAX_SCORE")));
		return scores;
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
		
		data.put("openId", data.get("userId"));
		data.put("score", -(int) data.get("score"));
		int gameId = (int) data.get("gameId");
		/*String sql = PropUtils.getSql("GameInfoService.getFriendsRank");
		List<Map<String,Object>> ret = commonDao.queryForList(sql, data);*/
		JSONObject j = new JSONObject();
		
		j.put("globalRank", localStorage.getGlobalRank(gameId, (int)data.get("score")));
		j.put("globalUserCnt", localStorage.getGlobalUserCnt(gameId));
		/*j.put("friendsRank", ret.get(0).get("cnt"));
		j.put("friendsCnt", ret.get(1).get("cnt"));*/
		j.put("friendsRank", 1);
		j.put("friendsCnt", 1);
		j.put("kingScore", -localStorage.getKingScore(gameId));
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
