package com.xrpc.cluster.ha.support;

import com.xrpc.cluster.ha.AbstractHaStrategy;
import com.xrpc.rpc.Response;
import com.xrpc.rpc.client.Invoker;

public class FailfastHaStrategy extends AbstractHaStrategy {

	@Override
	public Response call(Invoker invoker) throws Throwable {
		return invoker.invoke();
	}
}
