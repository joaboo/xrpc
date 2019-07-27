package com.xrpc.common.threadpool;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ThreadPoolConfig {
	private String name;
	private Integer cores;
	private Integer threads;
	private Long alive;
	private Integer queues;
}
