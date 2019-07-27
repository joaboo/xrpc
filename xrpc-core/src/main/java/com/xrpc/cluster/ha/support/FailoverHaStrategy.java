package com.xrpc.cluster.ha.support;

import java.util.concurrent.TimeUnit;

import com.xrpc.cluster.ha.AbstractHaStrategy;
import com.xrpc.exception.RpcException;
import com.xrpc.rpc.Request;
import com.xrpc.rpc.Response;
import com.xrpc.rpc.client.Invoker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FailoverHaStrategy extends AbstractHaStrategy {

	@Override
	public Response call(Invoker invoker) throws Exception {
		Request request = invoker.getRequest();
		int retryNumber = invoker.getRetryNumber();
		if (retryNumber < 0) {
			retryNumber = 0;
		}
		for (int i = 0; i <= retryNumber; i++) {
			try {
				return invoker.invoke();
			} catch (RpcException e) {
				if (i >= retryNumber) {
					log.error("", e);
					throw e;
				}
				TimeUnit.MILLISECONDS.sleep(100);
				log.debug("Client retry [{}]", i + 1);
			} catch (Throwable e) {
				log.warn(String.format("FailOverHaStrategy Call false for request:%s", request), e);
			}
		}
		throw new RpcException("FailoverHaStrategy Call should not come here!");
	}

}