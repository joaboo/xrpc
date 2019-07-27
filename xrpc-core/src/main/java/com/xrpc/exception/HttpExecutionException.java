package com.xrpc.exception;

public class HttpExecutionException extends RpcException {
	private static final long serialVersionUID = 156279380649009256L;

	public HttpExecutionException(String message) {
		super(message);
	}

	public HttpExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpExecutionException(Throwable cause) {
		super(cause);
	}

}
