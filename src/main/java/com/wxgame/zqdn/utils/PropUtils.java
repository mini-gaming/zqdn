/**
 * 
 */
package com.wxgame.zqdn.utils;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fozhang
 *
 */
public class PropUtils {

	private static final Logger logger = LoggerFactory.getLogger(PropUtils.class);

	private static final String[] CONFIG_FILES = { "/flags.properties", "/queries.properties" };

	private static final Properties serviceConfig;

	private static final Properties sqlConfig;

	static {
		InputStream flagsIn = PropUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILES[0]);
		InputStream queriesIn = PropUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILES[1]);

		serviceConfig = new Properties();
		sqlConfig = new Properties();
		try {
			serviceConfig.load(flagsIn);
			sqlConfig.load(queriesIn);

		} catch (Exception e) {
			logger.error("Error load configuration properties. Service may not work for some functions.", e);
		}
	}


	public static String getSql(String key) {

		String _sql = sqlConfig.getProperty(key, null);
		return _sql;
	}

	public static String getServiceConfig(String key) {

		return serviceConfig.getProperty(key, null);
	}

	public static int getServiceConfigAsInt(String key) {

		try {
			String v = serviceConfig.getProperty(key, "0");
			return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

}
