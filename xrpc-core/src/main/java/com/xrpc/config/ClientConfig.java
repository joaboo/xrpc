package com.xrpc.config;

import java.util.List;

import com.xrpc.common.intercept.Interceptor;

import lombok.Data;

@Data
public class ClientConfig {

	private String name;
	private String serialization;

	private int maxConnections = 30;
	private long keepAliveTimeInMillis = 5 * 60 * 1000;

	private int threadPoolSize;
	private int timeoutInMillis;

	private String lbStrategy;
	private String haStrategy;

	private List<Class<?>> interfaceClassList;
	private List<ProviderAddress> providerList;

	private List<Interceptor> interceptors;

}
