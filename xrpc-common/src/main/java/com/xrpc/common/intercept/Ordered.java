package com.xrpc.common.intercept;

public interface Ordered {

	int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

	int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

	int getOrder();

}
