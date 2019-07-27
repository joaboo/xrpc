package com.xrpc.spring.bean;

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.xrpc.common.util.Assert;
import com.xrpc.config.ServerConfig;
import com.xrpc.exception.SpringConfigException;

import lombok.Data;

@Data
public class SpringServerConfigBean implements InitializingBean, ApplicationContextAware {

	private String name;

	private int port;
	private String serialization = "protostuff";

	private int threads = 100;
	private int queues = 100;

	private Set<String> interfaceList;

	private ApplicationContext applicationContext;

	public ServerConfig translate() throws SpringConfigException {
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setName(name);
		serverConfig.setSerialization(serialization);
		serverConfig.setThreads(threads);
		serverConfig.setQueues(queues);
		serverConfig.setPort(port);
		interfaceList.forEach(interfaceName -> {
			Class<?> interfaceClass;
			try {
				interfaceClass = Class.forName(interfaceName);
			} catch (ClassNotFoundException e) {
				throw new SpringConfigException("class not found when spring bean translate to server config.", e);
			}
			Object interfaceInstance = applicationContext.getBean(interfaceClass);
			serverConfig.addInterface(interfaceName, interfaceInstance);
		});
		return serverConfig;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			Assert.notEmpty(name, "name == null");
			Assert.notEmpty(serialization, "name == null");
			Assert.isTrue(port < 1024 || port > 65535, "port not in [1024,65535]");
			Assert.notEmpty(interfaceList, "interfaceList == null");
		} catch (Exception e) {
			throw new SpringConfigException(e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
