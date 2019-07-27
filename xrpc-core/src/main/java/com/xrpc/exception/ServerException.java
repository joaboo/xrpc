package com.xrpc.exception;

public class ServerException extends RpcException {
	private static final long serialVersionUID = 156279380649009256L;

	public ServerException(String message) {
		super(message);
	}

	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerException(Throwable cause) {
		super(cause);
	}

}
