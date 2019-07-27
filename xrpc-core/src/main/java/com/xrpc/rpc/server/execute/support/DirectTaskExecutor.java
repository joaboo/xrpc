package com.xrpc.rpc.server.execute.support;

import com.xrpc.rpc.server.execute.Task;
import com.xrpc.rpc.server.execute.TaskExecutor;

public class DirectTaskExecutor implements TaskExecutor {

	@Override
	public void execute(Task task) {
		task.run();
	}
}