/**
 * 
 */
package com.wxgame.zqdn.dao.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.wxgame.zqdn.dao.CommonDao;

/**
 * @author fozhang
 *
 */
@Repository("commonDao")
public class CommonDaoImpl implements CommonDao {

	@Autowired
	private NamedParameterJdbcTemplate zqdnJdbcTemplate;

	@Override
	public List<Map<String, Object>> queryForList(NamedParameterJdbcTemplate jdbcTemplate, String query,
			Map<String, Object> params) {

		if (jdbcTemplate == null || StringUtils.isEmpty(query)) {
			throw new IllegalArgumentException("jdbcTemplate and query can't be null or empty");
		}

		List<Map<String, Object>> ret = jdbcTemplate.queryForList(query, params);

		if (ret == null) {
			return Collections.emptyList();
		}
		return ret;
	}

	@Override
	public List<Map<String, Object>> queryForList(String query, Map<String, Object> params) {

		return queryForList(zqdnJdbcTemplate, query, params);
	}

	@Override
	public List<Map<String, Object>> queryForList(String query) {

		return queryForList(zqdnJdbcTemplate, query, null);
	}
	
	


	@Override
	public <T> List<T> queryForListType(String query, Class<T> clazz) {
		
		Map<String, Object> params = new HashMap<String,Object>();
		return zqdnJdbcTemplate.queryForList(query, params, clazz);
	}

	@Override
	public int queryForInt(NamedParameterJdbcTemplate jdbcTemplate, String query, Map<String, Object> params) {

		if (jdbcTemplate == null || StringUtils.isEmpty(query)) {
			throw new IllegalArgumentException("jdbcTemplate and query can't be null or empty");
		}

		int ret = jdbcTemplate.queryForObject(query, params, Integer.class);
		return ret;
	}

	@Override
	public int queryForInt(String query, Map<String, Object> params) {

		return queryForInt(zqdnJdbcTemplate, query, params);
	}

	@Override
	public int queryForInt(String query) {

		return queryForInt(zqdnJdbcTemplate, query, null);
	}

	@Override
	public int insert(NamedParameterJdbcTemplate jdbcTemplate, String sql, Map<String, Object> params) {

		if (jdbcTemplate == null || StringUtils.isEmpty(sql)) {
			throw new IllegalArgumentException("jdbcTemplate and sql can't be null or empty");
		}
		return jdbcTemplate.update(sql, params);
	}

	@Override
	public int insert(String sql, Map<String, Object> params) {

		return insert(zqdnJdbcTemplate, sql, params);
	}

	@Override
	public int delete(NamedParameterJdbcTemplate jdbcTemplate, String sql, Map<String, Object> params) {

		if (jdbcTemplate == null || StringUtils.isEmpty(sql)) {
			throw new IllegalArgumentException("jdbcTemplate and sql can't be null or empty");
		}
		return jdbcTemplate.update(sql, params);
	}

	@Override
	public int delete(String sql, Map<String, Object> params) {

		return delete(zqdnJdbcTemplate, sql, params);
	}

	@Override
	public int update(String sql, Map<String, Object> params) {
		
		return zqdnJdbcTemplate.update(sql, params);
	}

}