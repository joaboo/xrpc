package com.xrpc.cluster.ha;

import com.xrpc.common.extension.SPI;
import com.xrpc.rpc.Response;
import com.xrpc.rpc.client.Invoker;

@SPI("failfast")
public interface HaStrategy {
	Response call(Invoker invoker) throws Exception;
}
