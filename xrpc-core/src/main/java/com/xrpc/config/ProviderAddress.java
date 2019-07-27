package com.xrpc.config;

import java.util.Objects;

import com.xrpc.common.Constants;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProviderAddress {

	private String name;
	private String version = Constants.DEFAULT_VERSION;
	private String host;
	private int port;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ProviderAddress)) {
			return false;
		}
		ProviderAddress other = (ProviderAddress) o;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.version, other.version)) {
			return false;
		}
		if (!Objects.equals(this.host, other.host)) {
			return false;
		}
		if (this.port != other.port) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = result * 31 + (name == null ? 0 : name.hashCode());
		result = result * 31 + (version == null ? 0 : version.hashCode());
		result = result * 31 + (host == null ? 0 : host.hashCode());
		result = result * 31 + port;
		return result;
	}
}
