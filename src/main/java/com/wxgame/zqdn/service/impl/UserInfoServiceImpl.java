package com.wxgame.zqdn.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.dao.CommonDao;
import com.wxgame.zqdn.model.BasicHttpResponse;
import com.wxgame.zqdn.service.UserInfoService;
import com.wxgame.zqdn.utils.HttpClientUtils;
import com.wxgame.zqdn.utils.PropUtils;
import static com.wxgame.zqdn.utils.SysErrorEnum.DB_INSERT_ERR;
import static com.wxgame.zqdn.utils.SysErrorEnum.HTTP_CALL_ERR;
import static com.wxgame.zqdn.utils.BussErrorEnum.WEIXIN_API_ERR;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

	public static final String URL_CODETOSESSION = "https://api.weixin.qq.com/sns/jscode2session";

	@Autowired
	private CommonDao commonDao;

	private int addOrUpdateNewUser(Map<String, Object> user) {

		String sql = PropUtils.getSql("UserInfoService.addOrUpdateUser");
		return commonDao.insert(sql, user);
	}

	private int addOrUpdateUserGameMap(Map<String, Object> data) {

		data.put("gameId", 1);
		String sql = PropUtils.getSql("UserInfoService.addOrUpdateUserGameMap");
		commonDao.insert(sql, data);
		data.put("gameId", 2);
		commonDao.insert(sql, data);
		return 2;
	}

	private void buildRelationship(Map<String, Object> data) {

		Assert.notEmpty(data);
		int channel = (int) data.get("channel");
		String rcmndOpenId = (String) data.get("rcmndOpenId");
		if (channel == 1 && !StringUtils.isEmpty(rcmndOpenId)) {
			String sql = PropUtils.getSql("UserInfoService.checkUserRelationship");
			if (commonDao.queryForInt(sql, data) == 0) {
				String saveUserRelSql = PropUtils.getSql("UserInfoService.saveUserRelationship");
				commonDao.insert(saveUserRelSql, data);
			}
		}
	}

	@Override
	public BasicHttpResponse registerNewUser(Map<String, Object> data) {

		JSONObject sessionObject = codeToOpenId((String) data.get("code"));
		if (sessionObject == null) {
			logger.error(HTTP_CALL_ERR.getMsg());
			return BasicHttpResponse.error(HTTP_CALL_ERR.getCode(), HTTP_CALL_ERR.getMsg());
		}
		if (sessionObject.containsKey("openid")) {
			try {
				String openId = sessionObject.getString("openid");
				data.put("openId", openId);
				//1
				addOrUpdateNewUser(data);
				//2
				addOrUpdateUserGameMap(data);
				//3
				buildRelationship(data);
				JSONObject ret = new JSONObject();
				ret.put("userId", openId);

				return BasicHttpResponse.successResult(ret);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return BasicHttpResponse.error(DB_INSERT_ERR.getCode(), DB_INSERT_ERR.getMsg());
			}
		} else {
			String errMsg = WEIXIN_API_ERR.getMsg() + " : " + sessionObject.getString("errmsg");
			logger.error(errMsg);
			return BasicHttpResponse.error(WEIXIN_API_ERR.getCode(), errMsg);
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

	@Override
	public JSONObject codeToOpenId(String code) {

		Assert.isTrue(!StringUtils.isEmpty(code));

		RestTemplate restTemplate = HttpClientUtils.getHttpsRestTemplate();
	
		StringBuffer url = new StringBuffer(URL_CODETOSESSION);
		url.append("?appid=").append(PropUtils.getServiceConfig("appId"))
		.append("&secret=").append(PropUtils.getServiceConfig("appSecret"))
		.append("&grant_type=authorization_code&js_code=").append(code);

		logger.info("Call Weixin Request:\t"+url.toString());
		String res = restTemplate.getForObject(url.toString(), String.class);
		logger.info("Call Weixin Response:\t"+res);
		return JSON.parseObject(res);
	}

}
