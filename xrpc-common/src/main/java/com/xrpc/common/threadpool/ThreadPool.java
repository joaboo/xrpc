package com.xrpc.common.threadpool;

import java.util.concurrent.ExecutorService;

import com.xrpc.common.extension.SPI;

@SPI(value = "fixed", isSingleton = true)
public interface ThreadPool {

	ExecutorService getExecutor(ThreadPoolConfig config);

}
