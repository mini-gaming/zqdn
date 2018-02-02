package com.wxgame.zqdn.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.dao.CommonDao;
import com.wxgame.zqdn.model.BasicHttpResponse;
import com.wxgame.zqdn.service.UserInfoService;
import com.wxgame.zqdn.utils.PropUtils;
import static com.wxgame.zqdn.utils.SysErrorEnum.DB_INSERT_ERR;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

	@Autowired
	private CommonDao commonDao;

	private int addOrUpdateNewUser(Map<String, Object> user) {

		String sql = PropUtils.getSql("UserInfoService.addOrUpdateUser");
		return commonDao.insert(sql, user);
	}

	private int addOrUpdateUserGameMap(Map<String, Object> data) {

		String sql = PropUtils.getSql("UserInfoService.addOrUpdateUserGameMap");
		return commonDao.insert(sql, data);
	}
	
	private void buildRelationship(Map<String, Object> data){
		
		Assert.notEmpty(data);
		int channel = (int)data.get("channel");
		String rcmndOpenId = (String)data.get("rcmndOpenId");
		if(channel == 1 && !StringUtils.isEmpty(rcmndOpenId)){
			String sql = PropUtils.getSql("UserInfoService.checkUserRelationship");
			if(commonDao.queryForInt(sql, data) == 0){
				String saveUserRelSql = PropUtils.getSql("UserInfoService.saveUserRelationship");
				commonDao.insert(saveUserRelSql, data);
			}
		}
	}

	@Override
	public BasicHttpResponse registerNewUser(Map<String, Object> data) {

		try {
			addOrUpdateNewUser(data);
			addOrUpdateUserGameMap(data);
			buildRelationship(data);
			return BasicHttpResponse.success();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return BasicHttpResponse.error(DB_INSERT_ERR.getCode(), DB_INSERT_ERR.getMsg());
		}

	}

	@Override
	public BasicHttpResponse login(Map<String, Object> data) {

		String sql = PropUtils.getSql("UserInfoService.login");
		commonDao.insert(sql, data);
		JSONObject userInfo = getUserInfoByGame(data);
		return BasicHttpResponse.successResult(userInfo);
	}

	@Override
	public BasicHttpResponse logout(Map<String, Object> data) {

		String sql = PropUtils.getSql("UserInfoService.logout");
		commonDao.update(sql, data);
		return BasicHttpResponse.success();
	}

	@Override
	public JSONObject getUserInfoByGame(Map<String, Object> data) {

		String sql = PropUtils.getSql("UserInfoService.getUserInfoByGame");
		List<Map<String, Object>> ret = commonDao.queryForList(sql, data);
		if (CollectionUtils.isEmpty(ret)) {
			return null;
		}
		JSONObject obj = JSONObject.parseObject(JSON.toJSONString(ret.get(0)));
		return obj;
	}

}
