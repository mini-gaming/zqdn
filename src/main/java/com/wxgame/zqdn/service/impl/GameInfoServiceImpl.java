package com.wxgame.zqdn.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.dao.CommonDao;
import com.wxgame.zqdn.dao.LocalStorage;
import com.wxgame.zqdn.model.BasicHttpResponse;
import com.wxgame.zqdn.service.GameInfoService;
import com.wxgame.zqdn.utils.MyUtils;
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

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public BasicHttpResponse recordGameInstance(Map<String,Object> data) {
		
		String sql = PropUtils.getSql("GameInfoService.recordGameInstance");
		try {
			commonDao.insert(sql, data);
			int personalMax = (int) data.get("personalMax");
			//int gameMax = localStorage.getKingScore(gameId);
			int currScore = (int) data.get("score");
			if(currScore>personalMax){
				updatePersonalMaxScore(data);
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
			if(score > kingScore){
				localStorage.updateKingScore(gameId, score);
				kingScore = score;
			}
			
		}
		
		
			
		j.put("globalRank", globalRank);
		j.put("globalUserCnt", globalUserCnt);
		j.put("friendsRank", 1);
		j.put("friendsCnt", 1);
		j.put("kingScore", -kingScore);
		j.put("personalMaxScore", -personalMax);
		j.put("beatPercent", (globalUserCnt - globalRank)*1.0f/(globalUserCnt-1));
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

	@Override
	public List<String> readIdiom(int size) {
		
		Assert.isTrue(size > 0);
		String sql = PropUtils.getSql("GameInfoService.getIdioms");
		List<String> ret = commonDao.queryForListType(sql, String.class);
		int weekInx = MyUtils.getWeekIndex();
		int threshold = 6 + weekInx;
		int index = 0;
		
		List<String> idioms = new ArrayList<String>(size);
		while(index < size && threshold>0 && !ret.isEmpty()){
			
			Iterator<String> iter = ret.iterator();
			int _index = 1;
			while(iter.hasNext() && index < size){
				if(_index % threshold == 0){
					String v = iter.next();
					idioms.add(v);
					index++;
					iter.remove();
				}
				_index++;
			}
			threshold -- ;
			
		}
		return idioms;
	}

	@Override
	public void updateIdioms() {
		
		logger.info("Refresh idioms in cache");
		List<String> idioms = readIdiom(PropUtils.getServiceConfigAsInt("idiomSize"));
		localStorage.updateIdioms(idioms);
	}

	@Override
	public List<Integer> getAllGameIds() {
		
		String sql = PropUtils.getSql("GameInfoService.getAllGameIds");
		return commonDao.queryForListType(sql, Integer.class);
	}

	@Override
	public List<String> offerIdioms(List<Integer> idiomIndexArr) {
		
		return localStorage.offerIdioms(idiomIndexArr);
	}

	@Override
	public JSONObject analysisPlay() {
		
		String sql = PropUtils.getSql("GameInfoService.analysisPlay");
		List<Map<String, Object>> ret = commonDao.queryForList(sql);
		JSONObject _j = new JSONObject();
		List<String> log = new ArrayList<String>();
		if(!CollectionUtils.isEmpty(ret)){
			for(Map<String,Object> _r : ret){
				Date date = (Date)_r.get("dt");
				
				Integer hour = (Integer)_r.get("hr");
				long cnt = (long) _r.get("num");
				log.add(DateUtils.formatDate(date, "yyyy-MM-dd")+" H"+hour+" = "+cnt);
			}
		}
		_j.put("playGameCount", log);
		return _j;
	}

	@Override
	public JSONObject analysisVisit() {
		String sql = PropUtils.getSql("GameInfoService.analysisVisit");
		List<Map<String, Object>> ret = commonDao.queryForList(sql);
		JSONObject _j = new JSONObject();
		List<String> log = new ArrayList<String>();
		if(!CollectionUtils.isEmpty(ret)){
			for(Map<String,Object> _r : ret){
				Date date = (Date)_r.get("dt");
				Integer hour = (Integer)_r.get("hr");
				long cnt = (long) _r.get("num");
				log.add(DateUtils.formatDate(date, "yyyy-MM-dd")+" H"+hour+" = "+cnt);
			}
		}
		_j.put("loginGameCount", log);
		return _j;
	}

	

}
