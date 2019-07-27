package com.xrpc.exception;

public class ExecutionTimeoutException extends RpcException {
	private static final long serialVersionUID = 4267150376452541936L;

	public ExecutionTimeoutException(String message) {
		super(message);
	}

	public ExecutionTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExecutionTimeoutException(Throwable cause) {
		super(cause);
	}
}
