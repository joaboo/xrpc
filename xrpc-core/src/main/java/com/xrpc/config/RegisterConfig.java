package com.xrpc.config;

import java.util.List;

import lombok.Data;

@Data
public class RegisterConfig {

	private String scheme = "HTTP";
	private String host = "localhost";
	private int port = 0;
	private int healthCheckInterval = 10; // 10s
	private long refreshIntervalInMillis = 30 * 1000L; // 30s

	private List<String> serviceNames;

}
