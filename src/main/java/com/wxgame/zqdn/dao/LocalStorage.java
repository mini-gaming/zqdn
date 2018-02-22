package com.wxgame.zqdn.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.model.Idiom;

@Repository("localStorage")
public class LocalStorage {

	private ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<String, String>();
	
	private Map<Integer,SortedMap<Integer, Integer>> maxScoreMaps = new HashMap<Integer,SortedMap<Integer, Integer>>();

	private ConcurrentHashMap<Integer, Integer> kingScoreMap = new ConcurrentHashMap<Integer, Integer>();
	
	private List<Idiom> idioms = new ArrayList<Idiom>();
	
	public JSONObject peekMaxScoreCache(){
		
		JSONObject j = new JSONObject();
		
		int totalUser = getGlobalUserCnt(1);
		j.put("totalUserCnt", totalUser);
		j.put("kingScore", kingScoreMap);
		j.put("maxScore", maxScoreMaps);
		
		return j;
	}
	
	public JSONObject peekIdiomsCache(){
		
		JSONObject j = new JSONObject();
		j.put("idioms", idioms);
		j.put("cnt", idioms.size());
		return j;
		
		
	}
	

	public void put(String key, String val) {

		this.cache.put(key, val);
	}

	public String get(String key) {
		return this.cache.get(key);
	}

	public int getGlobalRank(int gameId, int score) {

		SortedMap<Integer, Integer> maxScoreMap = maxScoreMaps.get(gameId);
		int rank = 1;
		if (maxScoreMap.isEmpty()) {
			return rank;
		}
		Set<Integer> keys = maxScoreMap.keySet();

		Iterator<Integer> iter = keys.iterator();
		while (iter.hasNext()) {
			int maxScore = iter.next();
			int cnt = maxScoreMap.get(maxScore);
			if (maxScore > score) {
				rank += cnt;
			} else {
				break;
			}
		}

		return rank;

	}

	public int getGlobalUserCnt(int gameId) {
		
		SortedMap<Integer, Integer> maxScoreMap = maxScoreMaps.get(gameId);

		int size = maxScoreMap.size();
		if (size == 0) {
			return 1;
		}
		int total = 0;
		for(Integer i : maxScoreMap.values()){
			total += i;
		}
			
		return total;
	}
	
	public void updateForNewUser(){
		
		Iterator<Integer> iter = maxScoreMaps.keySet().iterator();
		while(iter.hasNext()){
			SortedMap<Integer, Integer> maxScoreMap = maxScoreMaps.get(iter.next());
			if(maxScoreMap != null){
				synchronized (maxScoreMap) {

					if (maxScoreMap.containsKey(Integer.MIN_VALUE)) {
						maxScoreMap.put(Integer.MIN_VALUE, maxScoreMap.get(Integer.MIN_VALUE) + 1);
					} else {
						maxScoreMap.put(Integer.MIN_VALUE, 1);
					}

				}
			}
		}
		
	}

	public void updateMaxScoreMap(int gameId, int oldScore, int newScore) {
		
		SortedMap<Integer, Integer> maxScoreMap = maxScoreMaps.get(gameId);
		
		if(oldScore == 0){
			oldScore = Integer.MIN_VALUE;
		}

		if (!maxScoreMap.containsKey(oldScore)) {
			throw new RuntimeException("MaxScoreMap cache doesn't work well");
		}

		synchronized (maxScoreMap) {

			maxScoreMap.put(oldScore, maxScoreMap.get(oldScore) - 1);
			if (maxScoreMap.containsKey(newScore)) {
				maxScoreMap.put(newScore, maxScoreMap.get(newScore) + 1);
			} else {
				maxScoreMap.put(newScore, 1);
			}

		}
	}
	
	public void initialMaxScoreMap(int gameId, List<Map<String, Object>> scores){
		
		if (!CollectionUtils.isEmpty(scores)) {
			
			SortedMap<Integer, Integer> _map = Collections
					.synchronizedSortedMap(new TreeMap<Integer, Integer>(new Comparator<Integer>() {

						@Override
						public int compare(Integer o1, Integer o2) {

							return o2 - o1;
						}

					}));
			for (Map<String, Object> m : scores) {
				int maxScore = (int) m.get("MAX_SCORE");
				if(maxScore == 0){
					maxScore = Integer.MIN_VALUE;
				}
				long cnt = (long) m.get("CNT");
				_map.put(maxScore, new Long(cnt).intValue());
			}
			maxScoreMaps.put(gameId, _map);
			
		}
		
	}

	public int getKingScore(int gameId) {

		return kingScoreMap.get(gameId);
	}

	public void updateKingScore(int gameId, int score) {

		synchronized (kingScoreMap) {
			kingScoreMap.put(gameId, score);
		}

	}
	
	public void initialKingScore(List<Map<String, Object>> scores){
		
		if (!CollectionUtils.isEmpty(scores)) {
			for (Map<String, Object> m : scores) {
				int gameId = (int) m.get("ID");
				int cnt = (int) m.get("MAX_SCORE");
				kingScoreMap.put(gameId, cnt);
			}
		}
	}
	
	public void updateIdioms(List<Idiom> idioms){
		
		synchronized(this.idioms){
			this.idioms.clear();
			this.idioms.addAll(idioms);
		}
		
	}
	
	public Set<Idiom> offerIdioms(int cnt){
		
		Assert.isTrue(cnt == 30);
		int pieceSize = 3, segmentSize=600;
	    int times = cnt/pieceSize;
	    int randomRange = segmentSize - 2;
		Set<Idiom> ret = new HashSet<Idiom>(cnt);
		for(int i=0;i<times;i++){
			int start = (int) Math.floor((Math.random() * randomRange)) + i * segmentSize;
			ret.addAll(this.idioms.subList(start, start+pieceSize));
		}
		
		return ret;
	}
	

}
