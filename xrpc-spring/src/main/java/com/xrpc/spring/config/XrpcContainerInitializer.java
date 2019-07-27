package com.xrpc.spring.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import com.xrpc.XrpcInitializer;
import com.xrpc.common.util.MapUtils;
import com.xrpc.config.ClientConfig;
import com.xrpc.config.RegisterConfig;
import com.xrpc.config.ServerConfig;
import com.xrpc.exception.SpringConfigException;
import com.xrpc.rpc.client.Client;
import com.xrpc.spring.bean.SpringClientConfigBean;
import com.xrpc.spring.bean.SpringRegisterConfigBean;
import com.xrpc.spring.bean.SpringServerConfigBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XrpcContainerInitializer implements BeanFactoryPostProcessor, ApplicationListener<ContextRefreshedEvent>, Ordered {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
		init(defaultListableBeanFactory);
		registerBeanDefinition(defaultListableBeanFactory);
	}

	private void registerBeanDefinition(DefaultListableBeanFactory defaultListableBeanFactory) {
		// client
		Map<String, Client> clients = XrpcInitializer.getInstance().getClients();
		clients.values().forEach(client -> {
			Map<Class<?>, Object> interfaceInstances = client.getInterfaceInstances();
			interfaceInstances.forEach((interfaceClass, interfaceInstance) -> {
				String interfaceName = interfaceClass.getName();
				BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(interfaceName);

				AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
				ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
				constructorArgumentValues.addGenericArgumentValue(interfaceClass);
				constructorArgumentValues.addGenericArgumentValue(interfaceInstance);
				beanDefinition.setBeanClass(ProxyFactoryBean.class);
				beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

				defaultListableBeanFactory.registerBeanDefinition(interfaceName, beanDefinition);
			});
		});
	}

	private void init(DefaultListableBeanFactory defaultListableBeanFactory) {
		XrpcInitializer initializer = XrpcInitializer.getInstance();

		Map<String, SpringClientConfigBean> springClientConfigBeans = defaultListableBeanFactory.getBeansOfType(SpringClientConfigBean.class);
		Map<String, SpringRegisterConfigBean> springRegisterConfigBeans = defaultListableBeanFactory.getBeansOfType(SpringRegisterConfigBean.class);

		if (MapUtils.isEmpty(springRegisterConfigBeans)) {
			throw new SpringConfigException("No register configuration found in spring context");
		}
		if (springRegisterConfigBeans.size() > 1) {
			throw new SpringConfigException("More than one register configuration found in spring context");
		}

		// init register
		SpringRegisterConfigBean springRegisterConfigBean = springRegisterConfigBeans.values().iterator().next();
		RegisterConfig registerConfig = springRegisterConfigBean.translate();
		List<String> clientNames = springClientConfigBeans.values().stream().map(SpringClientConfigBean::getName).collect(Collectors.toList());
		registerConfig.setServiceNames(clientNames);
		initializer.init(registerConfig);

		// init client
		List<ClientConfig> clientConfigs = springClientConfigBeans.values().stream().map(SpringClientConfigBean::translate).collect(Collectors.toList());
		initializer.init(clientConfigs);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// init server
		ApplicationContext applicationContext = event.getApplicationContext();
		SpringServerConfigBean springServerConfigBean = null;
		try {
			springServerConfigBean = applicationContext.getBean(SpringServerConfigBean.class);
		} catch (NoSuchBeanDefinitionException e) {
			log.info("xrpc run as client.");
		}
		if (springServerConfigBean != null) {
			ServerConfig serverConfig = springServerConfigBean.translate();
			XrpcInitializer.getInstance().init(serverConfig);
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}