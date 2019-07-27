package com.xrpc.rpc.http.jetty.server;

import com.xrpc.config.ServerConfig;
import com.xrpc.rpc.server.Server;
import com.xrpc.rpc.server.ServerFactory;

public class JettyHttpServerFactory implements ServerFactory {

	@Override
	public Server create(ServerConfig config) {
		return new JettyHttpServer(config);
	}

}
