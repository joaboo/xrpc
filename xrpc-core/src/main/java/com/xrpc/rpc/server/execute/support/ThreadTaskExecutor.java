package com.xrpc.rpc.server.execute.support;

import java.util.concurrent.ExecutorService;

import com.xrpc.common.extension.ExtensionLoader;
import com.xrpc.common.threadpool.ThreadPool;
import com.xrpc.common.threadpool.ThreadPoolConfig;
import com.xrpc.rpc.server.execute.Task;
import com.xrpc.rpc.server.execute.TaskExecutor;

public class ThreadTaskExecutor implements TaskExecutor {

	private final ExecutorService executor;

	public ThreadTaskExecutor(ThreadPoolConfig config) {
		this.executor = ExtensionLoader.getExtensionLoader(ThreadPool.class).getDefaultExtension().getExecutor(config);
	}

	@Override
	public void execute(Task task) {
		executor.execute(task);
	}

}
