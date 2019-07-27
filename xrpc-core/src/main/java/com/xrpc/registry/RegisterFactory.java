package com.xrpc.registry;

import com.xrpc.common.extension.SPI;
import com.xrpc.config.RegisterConfig;

@SPI(value = "register", isSingleton = true)
public interface RegisterFactory {

	Register create(RegisterConfig config);

}
