package com.xrpc.rpc.client;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xrpc.XrpcInitializer;
import com.xrpc.cluster.ha.HaStrategy;
import com.xrpc.cluster.loadbalance.LoadBalance;
import com.xrpc.common.Constants;
import com.xrpc.common.extension.ExtensionLoader;
import com.xrpc.common.threadpool.ThreadPoolConfig;
import com.xrpc.common.util.CollectionUtils;
import com.xrpc.config.ClientConfig;
import com.xrpc.config.ProviderAddress;
import com.xrpc.exception.RpcException;
import com.xrpc.registry.Register;
import com.xrpc.rpc.Request;
import com.xrpc.rpc.Response;
import com.xrpc.rpc.RpcRequest;
import com.xrpc.rpc.RpcResponse;
import com.xrpc.rpc.client.execute.Task;
import com.xrpc.rpc.client.execute.TaskExecutor;
import com.xrpc.rpc.client.execute.support.ThreadPoolTaskExecutor;
import com.xrpc.rpc.client.proxy.ClientInvocationHandler;
import com.xrpc.serialize.Serializer;

public abstract class AbstractClient implements Client {

	protected final String name;
	protected final Register register;
	protected final TaskExecutor taskExecutor;
	protected final HaStrategy haStrategy;
	protected final LoadBalance loadBalance;
	protected final Serializer serializer;
	protected final ClientConfig clientConfig;
	private final Map<Class<?>, Object> interfaceInstanceCache = new ConcurrentHashMap<>();

	public AbstractClient(ClientConfig clientConfig) {
		this.name = clientConfig.getName();
		this.clientConfig = clientConfig;

		ThreadPoolConfig config = ThreadPoolConfig.builder().threads(clientConfig.getThreadPoolSize()).build();
		this.taskExecutor = new ThreadPoolTaskExecutor(config);

		this.haStrategy = ExtensionLoader.getExtensionLoader(HaStrategy.class).getExtension(this.clientConfig.getHaStrategy());
		this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(this.clientConfig.getLbStrategy());
		this.register = XrpcInitializer.getInstance().getRegister();
		this.serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(this.clientConfig.getSerialization());

		List<Class<?>> interfaceClassList = clientConfig.getInterfaceClassList();
		for (Class<?> interfaceClass : interfaceClassList) {
			interfaceInstanceCache.put(interfaceClass, build(interfaceClass));
		}
	}

	private Object build(Class<?> clazz) {
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new ClientInvocationHandler(this));
	}

	protected Request getRequest(Method method, Object[] args) {
		return new RpcRequest(0L, method, args);
	}

	protected ProviderAddress getProviderAddress() {
		List<ProviderAddress> availableAddresses;
		if (CollectionUtils.isNotEmpty(clientConfig.getProviderList())) {
			availableAddresses = clientConfig.getProviderList();
		} else {
			availableAddresses = register.getAvailableAddresses(name, Constants.DEFAULT_VERSION);
		}
		return loadBalance.select(availableAddresses);
	}

	protected Response request(Request request) throws Throwable {
		byte[] requestBytes = serializer.serialize(request);
		byte[] responseBytes = doRequest(requestBytes);
		return serializer.deserialize(responseBytes, RpcResponse.class);
	}

	protected abstract byte[] doRequest(byte[] request) throws RpcException;

	@Override
	public Object invoke(Method method, Object[] args) throws Throwable {
		return taskExecutor.execute(new InvokeTask(method, args));
	}

	class InvokeTask implements Task {

		final Method method;
		final Object[] args;

		InvokeTask(Method method, Object[] args) {
			this.method = method;
			this.args = args;
		}

		@Override
		public Object call() throws Exception {
			try {
				Request request = getRequest(method, args);
				Response response = haStrategy.call(new Invoker() {
					@Override
					public Response invoke() throws Throwable {
						return request(request);
					}

					@Override
					public Request getRequest() {
						return request;
					}
				});
				if (response.getException() != null) {
					throw response.getException();
				}
				return response.getResult();
			} catch (RpcException e) {
				throw e;
			} catch (Throwable t) {
				throw new RpcException(t);
			}
		}

		@Override
		public long getTimeoutInMillis() {
			return clientConfig.getTimeoutInMillis();
		}

		@Override
		public String getName() {
			return name;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	@Override
	public Map<Class<?>, Object> getInterfaceInstances() {
		return interfaceInstanceCache;
	}
}
