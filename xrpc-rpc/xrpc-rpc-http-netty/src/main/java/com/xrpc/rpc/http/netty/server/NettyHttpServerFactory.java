package com.xrpc.rpc.http.netty.server;

import com.xrpc.config.ServerConfig;
import com.xrpc.rpc.server.Server;
import com.xrpc.rpc.server.ServerFactory;

public class NettyHttpServerFactory implements ServerFactory {

	@Override
	public Server create(ServerConfig config) {
		return new NettyHttpServer(config);
	}

}
