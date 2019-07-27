package com.xrpc.cluster.loadbalance;

import java.util.List;

import com.xrpc.common.extension.SPI;
import com.xrpc.config.ProviderAddress;

@SPI("roundrobin")
public interface LoadBalance {

	ProviderAddress select(List<ProviderAddress> addresses);

}
