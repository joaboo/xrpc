package com.xrpc.rpc.client.execute.support;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.xrpc.common.extension.ExtensionLoader;
import com.xrpc.common.threadpool.ThreadPool;
import com.xrpc.common.threadpool.ThreadPoolConfig;
import com.xrpc.exception.ExecutionTimeoutException;
import com.xrpc.rpc.client.execute.Task;
import com.xrpc.rpc.client.execute.TaskExecutor;

public class ThreadPoolTaskExecutor implements TaskExecutor {

	private final ExecutorService executor;

	public ThreadPoolTaskExecutor(ThreadPoolConfig config) {
		this.executor = ExtensionLoader.getExtensionLoader(ThreadPool.class).getDefaultExtension().getExecutor(config);
	}

	@Override
	public Object execute(Task task) throws Throwable {
		try {
			Future<Object> future = executor.submit(task);
			return future.get(task.getTimeoutInMillis(), TimeUnit.MILLISECONDS);
		} catch (TimeoutException | InterruptedException e) {
			throw new ExecutionTimeoutException("Task(" + task.getName() + ") execution timeout(" + task.getTimeoutInMillis() + ").", e);
		} catch (ExecutionException e) {
			throw e;
		}
	}
}
