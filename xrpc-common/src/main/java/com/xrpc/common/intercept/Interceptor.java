package com.xrpc.common.intercept;

public interface Interceptor {

	Object intercept(Invocation invocation) throws Throwable;
}
