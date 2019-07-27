package com.xrpc.demo.consumer.config;

import java.util.HashSet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xrpc.spring.EnableXrpc;
import com.xrpc.spring.bean.SpringClientConfigBean;
import com.xrpc.spring.bean.SpringRegisterConfigBean;

@Configuration
@EnableXrpc
public class XrpcConfig {

	@Bean
	public SpringRegisterConfigBean springRegisterConfigBean() {
		SpringRegisterConfigBean registerConfigBean = new SpringRegisterConfigBean();
		registerConfigBean.setHost("106.14.191.175");
		registerConfigBean.setPort(8500);
		return registerConfigBean;
	}

	@Bean
	public SpringClientConfigBean demoClientConfig() {
		SpringClientConfigBean clientConfigBean = new SpringClientConfigBean();
		clientConfigBean.setName("xrpc-demo-provider");
		clientConfigBean.setInterfaceList(new HashSet<String>() {
			private static final long serialVersionUID = 6952212712110432247L;
			{
				add(com.xrpc.demo.api.service.DemoService.class.getName());
			}
		});
		return clientConfigBean;
	}

}
