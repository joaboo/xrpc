package com.xrpc.rpc.client;

import com.xrpc.rpc.Request;
import com.xrpc.rpc.Response;

public interface Invoker {

	default int getRetryNumber() {
		return 0;
	}

	Request getRequest();

	Response invoke() throws Throwable;

}
