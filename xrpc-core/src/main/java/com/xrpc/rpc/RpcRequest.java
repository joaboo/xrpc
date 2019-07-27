package com.xrpc.rpc;

import java.lang.reflect.Method;

import com.xrpc.rpc.Request;

public class RpcRequest implements Request {
	private static final long serialVersionUID = -8141023775141290084L;

	private long requestId;
	private String interfaceName;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] arguments;

	public RpcRequest() {
	}

	public RpcRequest(long requestId, Method method, Object[] arguments) {
		this.requestId = requestId;
		this.interfaceName = method.getDeclaringClass().getName();
		this.methodName = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.arguments = arguments;
	}

	@Override
	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	@Override
	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	@Override
	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

}
