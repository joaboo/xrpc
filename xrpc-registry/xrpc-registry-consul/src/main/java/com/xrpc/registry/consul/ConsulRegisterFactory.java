package com.xrpc.registry.consul;

import com.xrpc.config.RegisterConfig;
import com.xrpc.registry.Register;
import com.xrpc.registry.RegisterFactory;

public class ConsulRegisterFactory implements RegisterFactory {

	@Override
	public Register create(RegisterConfig config) {
		return new ConsulRegister(config);
	}

}
