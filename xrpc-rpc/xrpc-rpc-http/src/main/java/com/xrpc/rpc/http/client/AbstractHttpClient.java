package com.xrpc.rpc.http.client;

import com.xrpc.common.Constants;
import com.xrpc.common.util.StringUtils;
import com.xrpc.config.ClientConfig;
import com.xrpc.config.ProviderAddress;
import com.xrpc.rpc.client.AbstractClient;

public abstract class AbstractHttpClient extends AbstractClient {

	public AbstractHttpClient(ClientConfig clientConfig) {
		super(clientConfig);
	}

	protected String getUrl(ProviderAddress providerAddress) {
		return StringUtils.join(Constants.HTTP_PREFIX, providerAddress.getHost(), Constants.CHAR_COLON, providerAddress.getPort(), Constants.DEFAULT_RPC_REQUEST_PATH);
	}
}
