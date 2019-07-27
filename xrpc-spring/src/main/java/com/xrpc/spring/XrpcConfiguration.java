package com.xrpc.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xrpc.spring.config.XrpcContainerInitializer;

@Configuration
public class XrpcConfiguration {

	@Bean
	public XrpcContainerInitializer xrpcContainerInitializer() {
		return new XrpcContainerInitializer();
	}
}
