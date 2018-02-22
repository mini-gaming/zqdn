package com.wxgame.zqdn.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.model.BasicHttpResponse;
import com.wxgame.zqdn.model.Idiom;

public interface GameInfoService {
	
	public BasicHttpResponse recordGameInstance(Map<String,Object> data);
	
	public JSONObject getGameRank(Map<String,Object> data);
	
	public JSONObject getGameRankWithCache(Map<String,Object> data);
	
	public List<Integer> getAllGameIds();
	
	public List<Map<String, Object>> getAllMaxScoreMap(int gameId);
	
	public List<Map<String, Object>> getKingScoreMap();
	
	public int queryPersonalMaxScore(Map<String,Object> data);
	
	public List<Idiom> readIdiom(int size, int seed);
	
	public void updateIdioms(int seed);
	
	public Set<Idiom> offerIdioms(int cnt);
	
	public JSONObject analysisPlay();
	
	public JSONObject analysisVisit();

}
