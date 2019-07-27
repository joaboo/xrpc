package com.xrpc.cluster.loadbalance.support;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.xrpc.cluster.loadbalance.LoadBalance;
import com.xrpc.common.util.CollectionUtils;
import com.xrpc.config.ProviderAddress;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoundRobinLoadBalance implements LoadBalance {
	private final AtomicInteger counter = new AtomicInteger(0);

	@Override
	public ProviderAddress select(List<ProviderAddress> addresses) {
		if (CollectionUtils.isEmpty(addresses)) {
			return null;
		}

		int size = addresses.size();
		int count = counter.getAndIncrement();
		int index = count % size;
		if (index < 0) {
			index += size;
		}
		ProviderAddress providerAddress = addresses.get(index);
		log.debug("RoundRobinLoadBalance invoke counter({}), providerAddress({})!", count, providerAddress);
		return providerAddress;
	}

}
