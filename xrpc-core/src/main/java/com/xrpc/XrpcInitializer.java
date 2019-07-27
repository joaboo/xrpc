package com.xrpc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xrpc.common.extension.ExtensionLoader;
import com.xrpc.common.util.NetUtils;
import com.xrpc.config.ClientConfig;
import com.xrpc.config.ProviderAddress;
import com.xrpc.config.RegisterConfig;
import com.xrpc.config.ServerConfig;
import com.xrpc.registry.Register;
import com.xrpc.registry.RegisterFactory;
import com.xrpc.rpc.client.Client;
import com.xrpc.rpc.client.ClientFactory;
import com.xrpc.rpc.server.Server;
import com.xrpc.rpc.server.ServerFactory;

public class XrpcInitializer {
	private static final XrpcInitializer CONTEXT = new XrpcInitializer();

	private XrpcInitializer() {
	}

	public static XrpcInitializer getInstance() {
		return CONTEXT;
	}

	private Register register;
	private Server server;
	private Map<String, Client> clients = new ConcurrentHashMap<>();

	public Register getRegister() {
		return register;
	}

	public Server getServer() {
		return server;
	}

	public Map<String, Client> getClients() {
		return clients;
	}

	public void init(List<ClientConfig> configs) {
		configs.forEach(this::init);
	}

	private void init(ClientConfig config) {
		ClientFactory clientFactory = ExtensionLoader.getExtensionLoader(ClientFactory.class).getDefaultExtension();
		Client client = clientFactory.create(config);
		clients.put(client.getName(), client);
	}

	public void init(RegisterConfig config) {
		RegisterFactory registerFactory = ExtensionLoader.getExtensionLoader(RegisterFactory.class).getDefaultExtension();
		register = registerFactory.create(config);
		register.init();
	}

	public void init(ServerConfig config) {
		ServerFactory serverFactory = ExtensionLoader.getExtensionLoader(ServerFactory.class).getDefaultExtension();
		server = serverFactory.create(config);
		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

		ProviderAddress providerAddress = new ProviderAddress();
		providerAddress.setName(config.getName());
		providerAddress.setHost(NetUtils.getLocalHost());
		providerAddress.setPort(config.getPort());
		register.register(providerAddress);
	}
}
