package com.xrpc.rpc.client.proxy;

import java.lang.reflect.Method;

import com.xrpc.rpc.client.Client;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientInvocationHandler implements java.lang.reflect.InvocationHandler {

	private final Client client;

	public ClientInvocationHandler(Client client) {
		this.client = client;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == Object.class) {
			return method.invoke(this, args);
		}

		long begin = System.currentTimeMillis();
		try {
			return client.invoke(method, args);
		} catch (Throwable t) {
			log.error("Throwable Caught When Invoking " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "!", t);
			throw t;
		} finally {
			long timeCost = System.currentTimeMillis() - begin;
			log.debug("Invoking " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + " Costs " + timeCost + "ms!");
		}
	}
}
