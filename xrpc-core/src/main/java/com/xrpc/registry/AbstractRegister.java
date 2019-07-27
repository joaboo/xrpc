package com.xrpc.registry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.xrpc.common.Constants;
import com.xrpc.config.ProviderAddress;
import com.xrpc.config.RegisterConfig;

public abstract class AbstractRegister implements Register {

	private final Map<ProviderAddressCacheKey, List<ProviderAddress>> providerAddressCache = new ConcurrentHashMap<>();
	private final AtomicBoolean init = new AtomicBoolean(false);

	protected final RegisterConfig registerConfig;

	protected AbstractRegister(RegisterConfig registerConfig) {
		this.registerConfig = registerConfig;
	}

	@Override
	public void init() {
		if (init.compareAndSet(false, true)) {
			doInit();
		}
	}

	protected abstract void doInit();

	protected List<ProviderAddress> getProvideraddressCache(String name, String version) {
		List<ProviderAddress> providerAddresses = providerAddressCache.get(new ProviderAddressCacheKey(name, version));
		if (providerAddresses == null) {
			return Collections.emptyList();
		}
		return providerAddresses;
	}

	protected void replaceProvideraddressCache(String name, String version, List<ProviderAddress> providerAddresses) {
		if (providerAddresses == null) {
			providerAddresses = Collections.emptyList();
		}
		providerAddressCache.put(new ProviderAddressCacheKey(name, version), providerAddresses);
	}

	protected String getServiceId(ProviderAddress address) {
		return address.getHost() + Constants.CHAR_HYPHEN + address.getPort() + Constants.CHAR_HYPHEN + address.getName() + Constants.CHAR_HYPHEN + address.getVersion();
	}

	protected String getHealthCheckUrl(ProviderAddress address) {
		return Constants.HTTP_PREFIX + address.getHost() + Constants.CHAR_COLON + address.getPort() + Constants.DEFAULT_HEALTH_CHECK_PATH;
	}

	class ProviderAddressCacheKey {
		private final String name;
		private final String version;

		public ProviderAddressCacheKey(String name, String version) {
			this.name = name;
			this.version = version;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof ProviderAddressCacheKey)) {
				return false;
			}
			ProviderAddressCacheKey other = (ProviderAddressCacheKey) o;
			if (!Objects.equals(this.name, other.name)) {
				return false;
			}
			if (!Objects.equals(this.version, other.version)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			int result = 1;
			result = result * 31 + (name == null ? 0 : name.hashCode());
			result = result * 31 + (version == null ? 0 : version.hashCode());
			return result;
		}
	}
}
