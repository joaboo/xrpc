package com.xrpc.rpc.http.jetty.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HealthcheckServlet extends HttpServlet {
	private static final long serialVersionUID = 7920341838816646336L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		log.debug("HealthcheckServlet [host=" + request.getRemoteAddr() + ",port=" + request.getRemotePort() + "]");
	}
}