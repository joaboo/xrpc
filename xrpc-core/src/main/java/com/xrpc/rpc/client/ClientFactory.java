package com.xrpc.rpc.client;

import com.xrpc.common.extension.SPI;
import com.xrpc.config.ClientConfig;

@SPI(value = "client", isSingleton = true)
public interface ClientFactory {

	Client create(ClientConfig config);

}
