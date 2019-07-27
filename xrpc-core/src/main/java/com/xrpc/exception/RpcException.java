package com.xrpc.exception;

public class RpcException extends RuntimeException {
	private static final long serialVersionUID = -7224867149400633292L;

	public RpcException(String message) {
		super(message);
	}

	public RpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public RpcException(Throwable cause) {
		super(cause);
	}

}
