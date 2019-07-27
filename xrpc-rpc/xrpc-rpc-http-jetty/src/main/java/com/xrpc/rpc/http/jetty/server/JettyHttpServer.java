package com.xrpc.rpc.http.jetty.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.xrpc.common.Constants;
import com.xrpc.config.ServerConfig;
import com.xrpc.exception.ServerException;
import com.xrpc.rpc.http.jetty.server.servlet.DispatcherServlet;
import com.xrpc.rpc.http.jetty.server.servlet.HealthcheckServlet;
import com.xrpc.rpc.http.server.AbstractHttpServer;
import com.xrpc.rpc.server.execute.support.DirectTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JettyHttpServer extends AbstractHttpServer {

	private org.eclipse.jetty.server.Server server;

	public JettyHttpServer(ServerConfig serverConfig) {
		super(serverConfig, new DirectTaskExecutor());
	}

	@Override
	public void start() {
		try {
			init();
			server.start();
			log.info("HttpServer has started![port=" + serverConfig.getPort() + "]");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServerException("Failed to start jetty server on localhost:" + serverConfig.getPort() + ", cause: " + e.getMessage(), e);
		}
	}

	@Override
	public void stop() {
		if (server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private void init() {
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setDaemon(true);
		threadPool.setMaxThreads(serverConfig.getThreads());
		threadPool.setMinThreads(serverConfig.getThreads());
		server = new Server(threadPool);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		context.addServlet(new ServletHolder(new DispatcherServlet(this)), Constants.DEFAULT_DISPATCHER_URL_PATTERN);
		context.addServlet(new ServletHolder(new HealthcheckServlet()), Constants.DEFAULT_HEALTH_URL_PATTERN);

		ServerConnector connector = new ServerConnector(server);
		connector.setPort(serverConfig.getPort());
		server.addConnector(connector);
	}
}
