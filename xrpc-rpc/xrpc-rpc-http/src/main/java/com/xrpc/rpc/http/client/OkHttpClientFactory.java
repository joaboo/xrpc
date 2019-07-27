package com.xrpc.rpc.http.client;

import com.xrpc.config.ClientConfig;
import com.xrpc.rpc.client.Client;
import com.xrpc.rpc.client.ClientFactory;

public class OkHttpClientFactory implements ClientFactory {

	@Override
	public Client create(ClientConfig config) {
		return new OkHttpClient(config);
	}

}
