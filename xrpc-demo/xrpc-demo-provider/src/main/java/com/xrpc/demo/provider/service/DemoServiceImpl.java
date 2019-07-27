package com.xrpc.demo.provider.service;

import org.springframework.stereotype.Service;

import com.xrpc.demo.api.service.DemoService;

@Service
public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {
		return "Hello " + name;
	}

}