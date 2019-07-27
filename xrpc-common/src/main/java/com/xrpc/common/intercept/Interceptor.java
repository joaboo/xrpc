package com.xrpc.common.intercept;

import lombok.NonNull;

public interface Interceptor extends Comparable<Interceptor>, Ordered {

	Object intercept(Invocation invocation) throws Throwable;

	@Override
	default int compareTo(@NonNull Interceptor o) {
		int thisOrder = getOrder();
		int otherOrder = o.getOrder();
		if (thisOrder > otherOrder) {
			return 1;
		}
		if (thisOrder < otherOrder) {
			return -1;
		}
		return 0;
	}

	@Override
	default int getOrder() {
		return HIGHEST_PRECEDENCE;
	}
}
