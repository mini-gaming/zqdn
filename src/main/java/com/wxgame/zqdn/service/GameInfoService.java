package com.wxgame.zqdn.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.model.BasicHttpResponse;

public interface GameInfoService {
	
	public BasicHttpResponse recordGameInstance(Map<String,Object> data);
	
	public JSONObject getGameRank(Map<String,Object> data);
	
	public JSONObject getGameRankWithCache(Map<String,Object> data);
	
	public List<Integer> getAllGameIds();
	
	public List<Map<String, Object>> getAllMaxScoreMap(int gameId);
	
	public List<Map<String, Object>> getKingScoreMap();
	
	public int queryPersonalMaxScore(Map<String,Object> data);
	
	public List<String> readIdiom(int size);
	
	public void updateIdioms();
	
	public List<String> offerIdioms(List<Integer> idiomIndexArr);
	
	public JSONObject analysisPlay();
	
	public JSONObject analysisVisit();

}
