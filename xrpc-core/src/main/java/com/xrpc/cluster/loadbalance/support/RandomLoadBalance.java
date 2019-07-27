package com.xrpc.cluster.loadbalance.support;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.xrpc.cluster.loadbalance.LoadBalance;
import com.xrpc.common.util.CollectionUtils;
import com.xrpc.config.ProviderAddress;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomLoadBalance implements LoadBalance {

	@Override
	public ProviderAddress select(List<ProviderAddress> addresses) {
		if (CollectionUtils.isEmpty(addresses)) {
			return null;
		}

		int size = addresses.size();
		int index;
		if (size == 1) {
			index = 0;
		} else {
			index = ThreadLocalRandom.current().nextInt(size);
		}
		ProviderAddress providerAddress = addresses.get(index);
		log.debug("RandomLoadBalance random index({}), providerAddress({})!", index, providerAddress);
		return providerAddress;
	}

}
