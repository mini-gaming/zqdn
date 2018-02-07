package com.wxgame.zqdn.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository("localStorage")
public class LocalStorage {

	private ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<String, String>();
	

	private SortedMap<Integer, Integer> game1MaxScoreMap = Collections
			.synchronizedSortedMap(new TreeMap<Integer, Integer>(new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {

					return o2 - o1;
				}

			}));
	
	private SortedMap<Integer, Integer> game2MaxScoreMap = Collections
			.synchronizedSortedMap(new TreeMap<Integer, Integer>(new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {

					return o2 - o1;
				}

			}));

	private ConcurrentHashMap<Integer, Integer> kingScoreMap = new ConcurrentHashMap<Integer, Integer>();
	

	public void put(String key, String val) {

		this.cache.put(key, val);
	}

	public String get(String key) {
		return this.cache.get(key);
	}

	public int getGlobalRank(int gameId, int score) {

		SortedMap<Integer, Integer> maxScoreMap = null;
		if(gameId == 1){
			maxScoreMap = game1MaxScoreMap;
		}else{
			maxScoreMap = game2MaxScoreMap;
		}
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
		
		SortedMap<Integer, Integer> maxScoreMap = null;
		if(gameId == 1){
			maxScoreMap = game1MaxScoreMap;
		}else{
			maxScoreMap = game2MaxScoreMap;
		}

		int size = maxScoreMap.size();
		if (size == 0) {
			return 1;
		}
		return size;
	}

	public void updateMaxScoreMap(int gameId, int oldScore, int newScore) {
		
		SortedMap<Integer, Integer> maxScoreMap = null;
		if(gameId == 1){
			maxScoreMap = game1MaxScoreMap;
		}else{
			maxScoreMap = game2MaxScoreMap;
		}

		if (!maxScoreMap.containsKey(oldScore)) {
			throw new RuntimeException("MaxScoreMap cache doesn't work well");
		}

		synchronized (maxScoreMap) {

			maxScoreMap.put(oldScore, maxScoreMap.get("oldScore") - 1);
			if (maxScoreMap.containsKey(newScore)) {
				maxScoreMap.put(newScore, maxScoreMap.get(newScore) + 1);
			} else {
				maxScoreMap.put(newScore, 1);
			}

		}
	}

	public void initialGame1MaxScoreMap(List<Map<String, Object>> scores) {
		

		if (!CollectionUtils.isEmpty(scores)) {
			for (Map<String, Object> m : scores) {
				int maxScore = (int) m.get("MAX_SCORE");
				int cnt = (int) m.get("CNT");
				game1MaxScoreMap.put(maxScore, cnt);
			}
		}
	}
	
	public void initialGame2MaxScoreMap(List<Map<String, Object>> scores) {
		

		if (!CollectionUtils.isEmpty(scores)) {
			for (Map<String, Object> m : scores) {
				int maxScore = (int) m.get("MAX_SCORE");
				int cnt = (int) m.get("CNT");
				game2MaxScoreMap.put(maxScore, cnt);
			}
		}
	}

	public int getKingScore(int gameId) {

		return kingScoreMap.get(gameId);
	}

	public void updateKingScore(int gameId, int score) {

		if (score > getKingScore(gameId)) {
			synchronized (kingScoreMap) {
				kingScoreMap.put(gameId, score);
			}
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

}
