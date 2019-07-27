package com.xrpc.spring.bean;

import org.springframework.beans.factory.InitializingBean;

import com.xrpc.common.util.Assert;
import com.xrpc.config.RegisterConfig;
import com.xrpc.exception.SpringConfigException;

import lombok.Data;

@Data
public class SpringRegisterConfigBean implements InitializingBean {

	private String scheme = "HTTP";
	private String host;
	private int port;
	private int healthCheckInterval = 10; // 10s
	private long refreshIntervalInMillis = 30 * 1000L; // 30s

	public RegisterConfig translate() throws SpringConfigException {
		RegisterConfig registerConfig = new RegisterConfig();
		registerConfig.setScheme(scheme);
		registerConfig.setHost(host);
		registerConfig.setPort(port);
		registerConfig.setHealthCheckInterval(healthCheckInterval);
		registerConfig.setRefreshIntervalInMillis(refreshIntervalInMillis);
		return registerConfig;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			Assert.notEmpty(scheme, "scheme == null");
			Assert.notEmpty(host, "host == null");
			Assert.isTrue(port < 1024 || port > 65535, "port not in [1024,65535]");
		} catch (Exception e) {
			throw new SpringConfigException(e);
		}
	}
}
