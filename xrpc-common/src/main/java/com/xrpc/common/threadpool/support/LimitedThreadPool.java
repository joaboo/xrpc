package com.xrpc.common.threadpool.support;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xrpc.common.Constants;
import com.xrpc.common.threadpool.ThreadPool;
import com.xrpc.common.threadpool.ThreadPoolConfig;
import com.xrpc.common.util.NumberUtils;
import com.xrpc.common.util.StringUtils;

public class LimitedThreadPool implements ThreadPool {

	@Override
	public ExecutorService getExecutor(ThreadPoolConfig threadPoolConfig) {
		String threadName = StringUtils.defaultValue(threadPoolConfig.getName(), Constants.DEFAULT_THREAD_NAME);
		int cores = NumberUtils.defaultValue(threadPoolConfig.getCores(), Constants.DEFAULT_CORE_THREADS);
		int threads = NumberUtils.defaultValue(threadPoolConfig.getThreads(), Constants.DEFAULT_THREADS);
		int queues = NumberUtils.defaultValue(threadPoolConfig.getQueues(), Constants.DEFAULT_QUEUES);
		return new ThreadPoolExecutor(cores, threads, Long.MAX_VALUE, TimeUnit.MILLISECONDS,
				queues == 0 ? new SynchronousQueue<Runnable>() : (queues < 0 ? new LinkedBlockingQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queues)),
				new NamedThreadFactory(threadName, true), new AbortPolicyWithReport(threadName));
	}
}
