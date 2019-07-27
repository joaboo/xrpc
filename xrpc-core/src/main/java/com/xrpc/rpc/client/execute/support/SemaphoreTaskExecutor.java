package com.xrpc.rpc.client.execute.support;

import java.util.concurrent.Semaphore;

import com.xrpc.exception.NoAvailableWorkerException;
import com.xrpc.rpc.client.execute.Task;
import com.xrpc.rpc.client.execute.TaskExecutor;

@Deprecated
public class SemaphoreTaskExecutor implements TaskExecutor {

	private final Semaphore semaphore;

	public SemaphoreTaskExecutor(int permits) {
		this.semaphore = new Semaphore(permits);
	}

	@Override
	public Object execute(Task task) throws Throwable {
		if (!semaphore.tryAcquire()) {
			throw new NoAvailableWorkerException(String.format("Service(%s) has no available worker in client!", task.getName()));
		}

		try {
			return task.call();
		} catch (Throwable t) {
			throw t;
		} finally {
			semaphore.release();
		}
	}
}