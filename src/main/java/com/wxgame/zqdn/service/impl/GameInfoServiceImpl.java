package com.wxgame.zqdn.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.wxgame.zqdn.model.Idiom;
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
	public List<Idiom> readIdiom(int size, int seed) {
		
		Assert.isTrue(size > 0);
		String picPath = PropUtils.getServiceConfig("picPath");
		File picDir = new File(picPath);
		if(!picDir.isDirectory()){
			throw new IllegalArgumentException("Invalid picture path");
		}
		String[] pics = picDir.list(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				
				return name.endsWith(".jpg");
			}
			
		});
		
		List<String> ret = new ArrayList<String>(pics.length);
		Set<String> optionAll = new HashSet<String>();
		for(String pic : pics){
			String _word = pic.replace(".jpg", "");
			ret.add(_word);
			optionAll.addAll(Arrays.asList(_word.split("")));
		}
		List<String> optionWords = new ArrayList<String>(optionAll);
		
		if(seed == 0){
			seed = MyUtils.getWeekIndex();
		}
		
		int threshold = 6 + seed;
		int optionLength = optionWords.size();
		int index = 0, optionIdex=0;
		
		List<Idiom> idioms = new ArrayList<Idiom>(size);
		while(index < size && threshold>0 && !ret.isEmpty()){
			
			Iterator<String> iter = ret.iterator();
			int _index = 1;
			while(iter.hasNext() && index < size){
				if(_index % threshold == 0){
					String v = iter.next();
					Idiom idiom = new Idiom();
					idiom.setQuestion(v);
					while(idiom.getOptions().size()<10){
						if(optionIdex >= optionLength){
							optionIdex = 0;
						}
						String _w = optionWords.get(optionIdex++);
						if(!v.contains(_w)){
							idiom.getOptions().add(_w);
						}
						
					}
					idioms.add(idiom);
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
	public void updateIdioms(int seed) {
		
		logger.info("Refresh idioms in cache");
		List<Idiom> idioms = readIdiom(PropUtils.getServiceConfigAsInt("idiomSize"),seed);
		localStorage.updateIdioms(idioms);
	}

	@Override
	public List<Integer> getAllGameIds() {
		
		String sql = PropUtils.getSql("GameInfoService.getAllGameIds");
		return commonDao.queryForListType(sql, Integer.class);
	}

	@Override
	public Set<Idiom> offerIdioms(int cnt) {
		
		return localStorage.offerIdioms(cnt);
	}

	@Override
	public JSONObject analysisPlay() {
		
		String sql = PropUtils.getSql("GameInfoService.analysisPlay");
		List<Map<String, Object>> ret = commonDao.queryForList(sql);
		JSONObject _j = new JSONObject();
		List<String> log = new ArrayList<String>();
		if(!CollectionUtils.isEmpty(ret)){
			for(Map<String,Object> _r : ret){
				
				String dt = (String)_r.get("dt");
				long cnt = (long) _r.get("num");
				log.add(dt+" = "+cnt);
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
				String dt = (String)_r.get("dt");
				long cnt = (long) _r.get("num");
				log.add(dt+" = "+cnt);
			}
		}
		_j.put("loginGameCount", log);
		return _j;
	}

	

}
