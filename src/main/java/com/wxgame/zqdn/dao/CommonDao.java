/**
 * 
 */
package com.wxgame.zqdn.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * @author fozhang
 *
 */
public interface CommonDao {
	
	List<Map<String, Object>> queryForList(NamedParameterJdbcTemplate jdbcTemplate, String query, Map<String,Object> params);
	
	List<Map<String, Object>> queryForList(String query, Map<String,Object> params);
	
	List<Map<String, Object>> queryForList(String query);
	
	<T> List<T> queryForListType(String query, Class<T> clazz);
	
	int queryForInt(NamedParameterJdbcTemplate jdbcTemplate, String query, Map<String,Object> params);
	
	int queryForInt(String query, Map<String,Object> params);
	
	int queryForInt(String query);
	
	int insert(NamedParameterJdbcTemplate jdbcTemplate, String sql, Map<String,Object> params);
	
	int insert(String sql, Map<String,Object> params);
	
	int delete(NamedParameterJdbcTemplate jdbcTemplate, String sql, Map<String,Object> params);
	
	int delete(String sql, Map<String,Object> params);
	
	int update(String sql, Map<String,Object> params);

}
