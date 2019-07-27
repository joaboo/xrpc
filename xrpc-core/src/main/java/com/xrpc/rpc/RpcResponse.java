package com.xrpc.rpc;

import com.xrpc.rpc.Response;

public class RpcResponse implements Response {
	private static final long serialVersionUID = -690933512517534723L;

	private long requestId;
	private Object result;
	private Throwable exception;

	public RpcResponse() {
	}

	public RpcResponse(long requestId) {
		this.requestId = requestId;
	}

	public RpcResponse(long requestId, Object result) {
		this(requestId);
		this.result = result;
	}

	public RpcResponse(long requestId, Throwable exception) {
		this(requestId);
		this.exception = exception;
	}

	@Override
	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	@Override
	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

}
