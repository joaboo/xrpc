package com.xrpc.common.intercept;

import java.util.Collections;
import java.util.List;

public abstract class AbstractInvocation implements Invocation {

	protected final List<Interceptor> interceptors;
	private int pos = 0;

	public AbstractInvocation(final List<Interceptor> interceptors) {
		if (interceptors != null) {
			this.interceptors = interceptors;
			Collections.sort(this.interceptors);
		} else {
			this.interceptors = Collections.emptyList();
		}
	}

	public Object invoke() throws Throwable {
		if (pos < interceptors.size()) {
			return interceptors.get(pos++).intercept(this);
		} else {
			return invoke0();
		}
	}

	protected abstract Object invoke0() throws Throwable;
}
