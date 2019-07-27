package com.xrpc.exception;

public class NoAvailableWorkerException extends RpcException {
	private static final long serialVersionUID = 156279380649009256L;

	public NoAvailableWorkerException(String message) {
		super(message);
	}

	public NoAvailableWorkerException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoAvailableWorkerException(Throwable cause) {
		super(cause);
	}

}
