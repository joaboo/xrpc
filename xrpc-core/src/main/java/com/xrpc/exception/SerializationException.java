package com.xrpc.exception;

public class SerializationException extends RpcException {
	private static final long serialVersionUID = -7548853016904720141L;

	public SerializationException(String message) {
		super(message);
	}

	public SerializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializationException(Throwable cause) {
		super(cause);
	}

}
