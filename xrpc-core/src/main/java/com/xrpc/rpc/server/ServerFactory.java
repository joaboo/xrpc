package com.xrpc.rpc.server;

import com.xrpc.common.extension.SPI;
import com.xrpc.config.ServerConfig;

@SPI(value = "server", isSingleton = true)
public interface ServerFactory {

	Server create(ServerConfig config);

}
