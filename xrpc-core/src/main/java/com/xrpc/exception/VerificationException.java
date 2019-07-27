package com.xrpc.exception;

public class VerificationException extends RpcException {
	private static final long serialVersionUID = 156279380649009256L;

	public VerificationException(String message) {
		super(message);
	}

	public VerificationException(String message, Throwable cause) {
		super(message, cause);
	}

	public VerificationException(Throwable cause) {
		super(cause);
	}

}
