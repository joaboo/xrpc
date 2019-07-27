package com.xrpc.demo.consumer.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xrpc.demo.api.service.DemoService;

@RestController
public class DemoController {

	@Autowired
	private DemoService demoService;

	@GetMapping("/sayHello")
	public String sayHello(@RequestParam String name) {
		return demoService.sayHello(name);
	}

}
