package com.xrpc.rpc.http.server;

import com.xrpc.config.ServerConfig;
import com.xrpc.rpc.server.AbstractServer;
import com.xrpc.rpc.server.execute.TaskExecutor;

public abstract class AbstractHttpServer extends AbstractServer {

	public AbstractHttpServer(ServerConfig serverConfig) {
		super(serverConfig);
	}

	public AbstractHttpServer(ServerConfig serverConfig, TaskExecutor taskExecutor) {
		super(serverConfig, taskExecutor);
	}

}
