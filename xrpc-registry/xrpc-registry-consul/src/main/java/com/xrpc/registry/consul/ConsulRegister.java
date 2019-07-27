package com.xrpc.registry.consul;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import com.xrpc.common.Constants;
import com.xrpc.common.threadpool.support.NamedThreadFactory;
import com.xrpc.common.util.CollectionUtils;
import com.xrpc.config.ProviderAddress;
import com.xrpc.config.RegisterConfig;
import com.xrpc.registry.AbstractRegister;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsulRegister extends AbstractRegister {
	private final ConsulClient consulClient;
	private final ScheduledExecutorService refreshExecutorService;
	private final List<ProviderAddress> registeredProviderAddresses = new CopyOnWriteArrayList<>();

	public ConsulRegister(RegisterConfig registerConfig) {
		super(registerConfig);
		this.consulClient = new ConsulClient(registerConfig.getHost(), registerConfig.getPort());
		this.refreshExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("ConsulRegisterRefreshExecutor", true));
	}

	@Override
	public void doInit() {
		// scheduleAtFixedRate
		refreshExecutorService.scheduleWithFixedDelay(this::refresh, 0L, registerConfig.getRefreshIntervalInMillis(), TimeUnit.MILLISECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> registeredProviderAddresses.forEach(this::unregister)));
	}

	@Override
	public List<ProviderAddress> getAvailableAddresses(String name, String version) {
		return getProvideraddressCache(name, version);
	}

	@Override
	public void register(ProviderAddress providerAddress) {
		try {
			NewService newService = new NewService();
			newService.setId(getServiceId(providerAddress));
			newService.setName(providerAddress.getName());
			newService.setAddress(providerAddress.getHost());
			newService.setPort(providerAddress.getPort());
			newService.setTags(Arrays.asList(providerAddress.getVersion()));
			NewService.Check check = new NewService.Check();
			check.setHttp(getHealthCheckUrl(providerAddress));
			check.setInterval(registerConfig.getHealthCheckInterval() + "s");
			newService.setCheck(check);
			consulClient.agentServiceRegister(newService);
			// address.setId(newService.getId());
			registeredProviderAddresses.add(providerAddress);
			log.info("ConsulRegister register succeeded.(address=" + providerAddress + ")");
		} catch (Exception e) {
			log.error("ConsulRegister register failed.(address=" + providerAddress + ")", e);
		}
	}

	@Override
	public void unregister(ProviderAddress address) {
		try {
			consulClient.agentServiceDeregister(getServiceId(address));
		} catch (Exception e) {
			log.error("ConsulRegister unregister failed.(address=" + address + ")", e);
		}
	}

	@Override
	public void refresh() {
		List<String> serviceNames = registerConfig.getServiceNames();
		if (CollectionUtils.isEmpty(serviceNames)) {
			return;
		}

		long start = System.currentTimeMillis();
		final String version = Constants.DEFAULT_VERSION;
		for (String name : serviceNames) {
			try {
				List<ProviderAddress> providerAddresses = null;
				HealthServicesRequest healthServicesRequest = HealthServicesRequest.newBuilder().setTag(version)
						.setPassing(true).setQueryParams(QueryParams.DEFAULT).build();
				Response<List<HealthService>> response = consulClient.getHealthServices(name, healthServicesRequest);
				if (response != null) {
					List<HealthService> healthServices = response.getValue();
					if (CollectionUtils.isNotEmpty(healthServices)) {
						providerAddresses = new ArrayList<>(healthServices.size());
						for (HealthService healthService : healthServices) {
							ProviderAddress address = new ProviderAddress();
							address.setHost(healthService.getService().getAddress());
							address.setPort(healthService.getService().getPort().intValue());
							address.setName(name);
							address.setVersion(version);
							// address.setId(service.getService().getId());
							providerAddresses.add(address);
						}
					}
				}
				replaceProvideraddressCache(name, version, providerAddresses);
			} catch (Exception e) {
				log.error("ConsulRegister refresh error.(name=" + name + ")", e);
			}
		}
		long stop = System.currentTimeMillis();
		log.debug("ConsulRegister refresh finish.(cost={})", (stop - start));
	}
}
