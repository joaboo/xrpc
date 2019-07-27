package com.xrpc.registry;

import java.util.List;

import com.xrpc.config.ProviderAddress;

public interface Register {

	void init();

	List<ProviderAddress> getAvailableAddresses(String name, String version);

	void register(ProviderAddress providerAddress);

	void unregister(ProviderAddress providerAddress);

	void refresh();

}
