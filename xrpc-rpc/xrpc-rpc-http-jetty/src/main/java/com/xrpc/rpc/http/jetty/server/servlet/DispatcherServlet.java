package com.xrpc.rpc.http.jetty.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethod;

import com.xrpc.common.Constants;
import com.xrpc.common.util.IOUtils;
import com.xrpc.rpc.http.server.AbstractHttpServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 2491787250088200471L;

	private final AbstractHttpServer server;

	public DispatcherServlet(AbstractHttpServer server) {
		this.server = server;
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!HttpMethod.POST.is(request.getMethod())) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		try {
			server.getTaskExecutor().execute(() -> {
				processHttpRequest(request, response);
			});
		} catch (Exception e) {
			log.error("request is rejected by threadpool!", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "request is rejected by threadpool!");
		}
	}

	private void processHttpRequest(HttpServletRequest request, HttpServletResponse response) {
		byte[] responseBytes;
		try {
			byte[] requestBytes = IOUtils.toByteArray(request.getInputStream());
			responseBytes = server.invoke(requestBytes);
		} catch (Throwable t) {
			log.error("DispatcherServlet process http request fail.", t);
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DispatcherServlet process http request fail.");
			} catch (IOException e) {
				log.error("DispatcherServlet write response fail.", t);
			}
			return;
		}
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			response.setCharacterEncoding(Constants.DEFAULT_CHARSET);
			response.setStatus(HttpServletResponse.SC_OK);
			out.write(responseBytes);
			out.flush();
		} catch (Exception e) {
			log.error("DispatcherServlet write response fail.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.error("DispatcherServlet close response fail.", e);
				}
			}
		}
	}
}
