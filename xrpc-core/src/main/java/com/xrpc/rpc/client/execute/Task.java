package com.xrpc.rpc.client.execute;

import java.util.concurrent.Callable;

public interface Task extends Callable<Object> {

	String getName();

	long getTimeoutInMillis();

}
