package com.wxgame.zqdn.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.dao.CommonDao;
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

	@Override
	public BasicHttpResponse recordGameInstance(Map<String,Object> data) {
		
		String sql = PropUtils.getSql("GameInfoService.recordGameInstance");
		try {
			commonDao.insert(sql, data);
			int maxScore = queryMaxScore(data);
			int currScore = (int) data.get("score");
			if(currScore>maxScore){
				updateMaxScore(data);
			}
			return BasicHttpResponse.success();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return BasicHttpResponse.error(DB_INSERT_ERR.getCode(), DB_INSERT_ERR.getMsg());
		}
		
	}
	
	private int queryMaxScore(Map<String,Object> data){
		
		String sql = PropUtils.getSql("GameInfoService.queryMaxScore");
		return commonDao.queryForInt(sql,data);
	}
	
	private int updateMaxScore(Map<String,Object> data){
		
		String sql = PropUtils.getSql("GameInfoService.updateMaxScore");
		return commonDao.update(sql, data);
	}
	

	@Override
	public JSONObject getGameRank(Map<String, Object> data) {
		
		String sql = PropUtils.getSql("GameInfoService.getGameRank");
		List<Map<String,Object>> ret = commonDao.queryForList(sql, data);
		JSONObject j = new JSONObject();
		j.put("globalRank", ret.get(0).get("rank"));
		j.put("friendsRank", ret.get(1).get("rank"));
		return j;
	}

	

}
