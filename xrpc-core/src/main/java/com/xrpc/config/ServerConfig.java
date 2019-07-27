package com.xrpc.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ServerConfig {

	private String name;

	private int port;
	private String serialization;

	private int threads = 100;
	private int queues = 100;

	private Map<String, Object> interfaceMap;

	public void addInterface(String interfaceName, Object interfaceInstance) {
		if (interfaceMap == null) {
			interfaceMap = new HashMap<>();
		}
		interfaceMap.put(interfaceName, interfaceInstance);
	}
}
