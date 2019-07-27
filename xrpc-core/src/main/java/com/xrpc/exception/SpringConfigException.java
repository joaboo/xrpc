package com.xrpc.exception;

public class SpringConfigException extends RpcException {
	private static final long serialVersionUID = 156279380649009256L;

	public SpringConfigException(String message) {
		super(message);
	}

	public SpringConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public SpringConfigException(Throwable cause) {
		super(cause);
	}

}
