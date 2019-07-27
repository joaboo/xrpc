package com.xrpc.rpc.http.client;

import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import com.xrpc.common.Constants;
import com.xrpc.common.util.StringUtils;
import com.xrpc.config.ClientConfig;
import com.xrpc.config.ProviderAddress;
import com.xrpc.exception.HttpExecutionException;
import com.xrpc.exception.RpcException;

import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpClient extends AbstractHttpClient {

	private final okhttp3.OkHttpClient httpClient;

	public OkHttpClient(ClientConfig clientConfig) {
		super(clientConfig);
		this.httpClient = new okhttp3.OkHttpClient.Builder()
				.callTimeout(clientConfig.getTimeoutInMillis(), TimeUnit.MILLISECONDS)
				.connectionPool(new ConnectionPool(clientConfig.getMaxConnections(), clientConfig.getKeepAliveTimeInMillis(), TimeUnit.MILLISECONDS))
				.dispatcher(new Dispatcher()).build();
	
	}

	@Override
	protected byte[] doRequest(byte[] request) throws RpcException {
		ProviderAddress providerAddress = getProviderAddress();
		String url = getUrl(providerAddress);
		Response response;
		try {
			RequestBody body = RequestBody.create(MediaType.get(Constants.DEFAULT_CONTENT_TYPE), request);
			Request httpRequest = new Request.Builder().url(url).post(body).build();
			Call call = httpClient.newCall(httpRequest);
			response = call.execute();
		} catch (Exception e) {
			throw new HttpExecutionException(StringUtils.join("Service(", getName(), ") fails to execute HttpRequest"), e);
		}
		int status = response.code();
		if (status != HttpURLConnection.HTTP_OK) {
			throw new HttpExecutionException(StringUtils.join("Service(", getName(), ") receives non-OK status(", status, ")"));
		}
		try {
			return response.body().bytes();
		} catch (Exception e) {
			throw new HttpExecutionException("Service(" + getName() + ") fails to read responsebody", e);
		}
	}
}
