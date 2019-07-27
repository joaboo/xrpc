package com.xrpc.rpc.client.intercept;

import java.util.List;

import com.xrpc.common.intercept.AbstractInvocation;
import com.xrpc.common.intercept.Interceptor;
import com.xrpc.rpc.client.execute.Task;

public class ClientInvocation extends AbstractInvocation {

	private final Task task;

	public ClientInvocation(Task task, List<Interceptor> interceptors) {
		super(interceptors);
		this.task = task;
	}

	@Override
	protected Object invoke0() throws Throwable {
		return task.call();
	}

}
