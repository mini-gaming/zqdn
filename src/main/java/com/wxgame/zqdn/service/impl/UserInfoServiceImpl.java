package com.wxgame.zqdn.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wxgame.zqdn.dao.CommonDao;
import com.wxgame.zqdn.dao.LocalStorage;
import com.wxgame.zqdn.model.BasicHttpResponse;
import com.wxgame.zqdn.service.UserInfoService;
import com.wxgame.zqdn.utils.HttpClientUtils;
import com.wxgame.zqdn.utils.MyEncoder;
import com.wxgame.zqdn.utils.PropUtils;
import static com.wxgame.zqdn.utils.SysErrorEnum.DB_INSERT_ERR;
import static com.wxgame.zqdn.utils.SysErrorEnum.HTTP_CALL_ERR;
import static com.wxgame.zqdn.utils.BussErrorEnum.WEIXIN_API_ERR;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	public static final String URL_CODETOSESSION = "https://api.weixin.qq.com/sns/jscode2session";

	@Autowired
	private CommonDao commonDao;
	
	@Autowired
	private LocalStorage localStorage;

	private int addOrUpdateNewUser(Map<String, Object> user) {

		String sql = PropUtils.getSql("UserInfoService.addOrUpdateUser");
		return commonDao.insert(sql, user);
	}
	
	private int addNewUser(Map<String, Object> user) {

		String sql = PropUtils.getSql("UserInfoService.addNewUser");
		return commonDao.insert(sql, user);
	}
	
	private int updateUser(Map<String, Object> user) {

		String sql = PropUtils.getSql("UserInfoService.updateUser");
		return commonDao.insert(sql, user);
	}

	private int addOrUpdateUserGameMap(Map<String, Object> data) {

		List<Integer> allGameIds = localStorage.getAllGameIds();
		String sql = PropUtils.getSql("UserInfoService.addOrUpdateUserGameMap");
		for(Integer gameId : allGameIds){
			data.put("gameId", gameId);
			
			commonDao.insert(sql, data);
		}

		return 2;
	}
	
	private int addNewUserGameMap(Map<String, Object> data) {

		List<Integer> allGameIds = localStorage.getAllGameIds();
		String sql = PropUtils.getSql("UserInfoService.addNewUserGameMap");
		StringBuffer sb = new StringBuffer(sql+" ");
		String openId = (String) data.get("openId");
		int channel = (int) data.get("channel");
		String rcmndOpenId = (String) data.get("rcmndOpenId");
		int authUserInfo = (int) data.get("authUserInfo");
		int authUserLocation = (int) data.get("authUserLocation");
		int authAddress = (int) data.get("authAddress");
		int authInvoiceTitle = (int) data.get("authInvoiceTitle");
		int authWeRun = (int) data.get("authWeRun");
		int authRecord = (int) data.get("authRecord");
		int authWritePhotosAlbum = (int) data.get("authWritePhotosAlbum");
		int authCamera = (int) data.get("authCamera");
		for(Integer gameId : allGameIds){
			
			sb.append("('").append(openId).append("',")
			.append(gameId).append(",")
			.append(channel).append(",")
			.append("'").append(rcmndOpenId).append("',")
			.append(authUserInfo).append(",")
			.append(authUserLocation).append(",")
			.append(authAddress).append(",")
			.append(authInvoiceTitle).append(",")
			.append(authWeRun).append(",")
			.append(authRecord).append(",")
			.append(authWritePhotosAlbum).append(",")
			.append(authCamera).append("),");
			
		}
		sql = sb.substring(0, sb.length()-1);
		return commonDao.insert(sql);
	}
	
	private int updateUserGameMap(Map<String, Object> data) {

		String sql = PropUtils.getSql("UserInfoService.updateUserGameMap");
		return commonDao.update(sql, data);
	}
	
	private boolean containUser(Map<String, Object> data){
		
		String sql = PropUtils.getSql("UserInfoService.containUser");
		int cnt = commonDao.queryForInt(sql, data);
		if(cnt > 0){
			return true;
		}
		return false;
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
		
		String logSql = PropUtils.getSql("UserInfoService.logVisit");
		commonDao.insert(logSql, data);
	}

	@Override
	public BasicHttpResponse registerNewUser(final Map<String, Object> data) {

		JSONObject sessionObject = codeToOpenId((String) data.get("code"));
		if (sessionObject == null) {
			logger.error(HTTP_CALL_ERR.getMsg());
			return BasicHttpResponse.error(HTTP_CALL_ERR.getCode(), HTTP_CALL_ERR.getMsg());
		}
		if (sessionObject.containsKey("openid")) {
			try {
				
				String openId = sessionObject.getString("openid");
				data.put("openId", openId);
				final boolean isNewUser = !containUser(data);
				if(isNewUser){
					localStorage.updateForNewUser();
					insertNewUser(data);
				}
				
				//insertOrUpdateUser(data);
				
				taskExecutor.execute(new Runnable(){

					@Override
					public void run() {
						
						if(!isNewUser){
							updateExistingUser(data);
						}
						buildRelationship(data);
						
					}}, 300000);
				
				JSONObject ret = new JSONObject();
				ret.put("userId", openId);
				ret.put("isNewUser", isNewUser);
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
	
	@Transactional(propagation=Propagation.REQUIRED)
	private void insertOrUpdateUser(Map<String, Object> data) {
		
		addOrUpdateNewUser(data);
		
		addOrUpdateUserGameMap(data);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	private void insertNewUser(Map<String, Object> data) {
		
		addNewUser(data);
		addNewUserGameMap(data);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	private void updateExistingUser(Map<String, Object> data) {
		
		updateUser(data);
		updateUserGameMap(data);
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
		
		String mode = PropUtils.getServiceConfig("mode");
		if("dev".equalsIgnoreCase(mode)){
			JSONObject _j = new JSONObject();
			_j.put("openid", "test_"+MyEncoder.md5EncodeStr(code));
			return _j;
		}

		RestTemplate restTemplate = HttpClientUtils.getHttpsRestTemplate();
	
		StringBuffer url = new StringBuffer(URL_CODETOSESSION);
		url.append("?appid=").append(PropUtils.getServiceConfig("appId"))
		.append("&secret=").append(PropUtils.getServiceConfig("appSecret"))
		.append("&grant_type=authorization_code&js_code=").append(code);

		//logger.info("Call Weixin Request:\t"+url.toString());
		String res = restTemplate.getForObject(url.toString(), String.class);
		logger.debug("Call Weixin Response:\t"+res);
		return JSON.parseObject(res);
	}

}
