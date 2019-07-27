package com.xrpc.rpc.client;

import java.lang.reflect.Method;
import java.util.Map;

import com.xrpc.config.ClientConfig;

public interface Client {

	String getName();

	ClientConfig getClientConfig();

	Object invoke(Method method, Object[] args) throws Throwable;

	Map<Class<?>, Object> getInterfaceInstances();

}
