package com.xrpc.rpc.server;

import java.util.Map;

import com.xrpc.config.ServerConfig;

public interface Server {

	String getName();

	ServerConfig getServerConfig();

	Map<String, Object> getInterfaceInstances();

	void start();

	void stop();
}
