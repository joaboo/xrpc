package com.xrpc.spring.bean;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;

import com.xrpc.common.Constants;
import com.xrpc.common.util.Assert;
import com.xrpc.common.util.CollectionUtils;
import com.xrpc.common.util.StringUtils;
import com.xrpc.config.ClientConfig;
import com.xrpc.config.ProviderAddress;
import com.xrpc.exception.SpringConfigException;

import lombok.Data;

@Data
public class SpringClientConfigBean implements InitializingBean {

	private String name;
	private String serialization = "protostuff";

	private int threadPoolSize = 20;
	private int timeoutInMillis = 5000;

	private String lbStrategy = "random";
	private String haStrategy = "fastfail";

	private Set<String> interfaceList;
	private Set<String> providerList;

	public void addInterface(String interfaceName) {
		if (interfaceList == null) {
			interfaceList = new HashSet<>();
		}
		interfaceList.add(interfaceName);
	}

	public void addProvider(String provider) {
		if (providerList == null) {
			providerList = new HashSet<>();
		}
		providerList.add(provider);
	}

	public ClientConfig translate() throws SpringConfigException {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setName(name);
		clientConfig.setSerialization(serialization);
		clientConfig.setThreadPoolSize(threadPoolSize);
		clientConfig.setTimeoutInMillis(timeoutInMillis);
		clientConfig.setLbStrategy(lbStrategy);
		clientConfig.setHaStrategy(haStrategy);
		clientConfig.setInterfaceClassList(interfaceList.stream().map(interfaceName -> {
			try {
				return Class.forName(interfaceName);
			} catch (ClassNotFoundException e) {
				throw new SpringConfigException("class not found when spring bean translate to server config.", e);
			}
		}).collect(Collectors.toList()));
		if (CollectionUtils.isNotEmpty(providerList)) {
			clientConfig.setProviderList(providerList.stream().map(providerAddr -> {
				try {
					String[] addr = StringUtils.split(providerAddr, Constants.CHAR_COLON);
					ProviderAddress providerAddress = new ProviderAddress();
					providerAddress.setHost(addr[0]);
					providerAddress.setPort(Integer.parseInt(addr[1]));
					return providerAddress;
				} catch (Exception e) {
					throw new SpringConfigException("providerList format is invalid", e);
				}
			}).collect(Collectors.toList()));
		}
		return clientConfig;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			Assert.notEmpty(name, "name == null");
			Assert.notEmpty(serialization, "serialization == null");
			Assert.notEmpty(lbStrategy, "lbStrategy == null");
			Assert.notEmpty(haStrategy, "haStrategy == null");
			Assert.notEmpty(interfaceList, "interfaceList == null");
		} catch (Exception e) {
			throw new SpringConfigException(e);
		}
	}
}
