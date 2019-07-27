package com.xrpc.rpc.client.execute;

public interface TaskExecutor {

	Object execute(Task task) throws Throwable;
}
