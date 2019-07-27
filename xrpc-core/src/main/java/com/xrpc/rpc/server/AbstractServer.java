package com.xrpc.rpc.server;

import java.lang.reflect.Method;
import java.util.Map;

import com.xrpc.common.extension.ExtensionLoader;
import com.xrpc.common.threadpool.ThreadPoolConfig;
import com.xrpc.config.ServerConfig;
import com.xrpc.exception.VerificationException;
import com.xrpc.rpc.RpcRequest;
import com.xrpc.rpc.RpcResponse;
import com.xrpc.rpc.server.execute.TaskExecutor;
import com.xrpc.rpc.server.execute.support.ThreadTaskExecutor;
import com.xrpc.serialize.Serializer;

public abstract class AbstractServer implements Server {

	protected final String name;
	protected final TaskExecutor taskExecutor;
	protected final Serializer serializer;
	protected final ServerConfig serverConfig;

	public AbstractServer(ServerConfig serverConfig) {
		this(serverConfig, new ThreadTaskExecutor(ThreadPoolConfig.builder().threads(serverConfig.getThreads())
				.queues(serverConfig.getQueues()).build()));
	}

	public AbstractServer(ServerConfig serverConfig, TaskExecutor taskExecutor) {
		this.name = serverConfig.getName();
		this.serverConfig = serverConfig;
		this.taskExecutor = taskExecutor;
		this.serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serverConfig.getSerialization());
	}

	public byte[] invoke(byte[] requestBytes) throws Throwable {
		RpcRequest request = serializer.deserialize(requestBytes, RpcRequest.class);
		RpcResponse response = new RpcResponse(request.getRequestId());
		try {
			Object target = serverConfig.getInterfaceMap().get(request.getInterfaceName());
			if (target == null) {
				throw new VerificationException("Interface(" + request.getInterfaceName() + ") not found.");
			}
			Method method = target.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
			Object result = method.invoke(target, request.getArguments());
			response.setResult(result);
		} catch (Throwable t) {
			response.setException(t);
		}
		return serializer.serialize(response);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	@Override
	public Map<String, Object> getInterfaceInstances() {
		return serverConfig.getInterfaceMap();
	}

	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}
}
