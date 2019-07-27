package com.xrpc.rpc.http.netty.server;

import com.xrpc.common.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private final NettyHttpServer server;

	public NettyHttpServerHandler(NettyHttpServer server) {
		this.server = server;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
		if (Constants.DEFAULT_HEALTH_CHECK_PATH.equals(httpRequest.uri())) {
			sendResponse(ctx, buildDefaultResponse("ok", HttpResponseStatus.OK));
			return;
		}
		if (!Constants.DEFAULT_RPC_REQUEST_PATH.equals(httpRequest.uri())) {
			sendResponse(ctx, buildDefaultResponse("not found!", HttpResponseStatus.NOT_FOUND));
			return;
		}

		httpRequest.content().retain();
		try {
			server.getTaskExecutor().execute(() -> {
				processHttpRequest(ctx, httpRequest);
			});
		} catch (Exception e) {
			log.error("request is rejected by threadpool!", e);
			httpRequest.content().release();
			sendResponse(ctx, buildDefaultResponse("request is rejected by threadpool!", HttpResponseStatus.INTERNAL_SERVER_ERROR));
		}
	}

	protected void processHttpRequest(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
		FullHttpResponse httpResponse = null;
		try {
			ByteBuf buf = httpRequest.content();
			final byte[] contentBytes = new byte[buf.readableBytes()];
			buf.getBytes(0, contentBytes);

			byte[] responseBytes = server.invoke(contentBytes);

			httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBytes));
			httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, Constants.DEFAULT_CONTENT_TYPE);
			httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
			if (HttpUtil.isKeepAlive(httpRequest)) {
				httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			} else {
				httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
			}
		} catch (Throwable t) {
			log.error("NettyHttpHandler process http request fail.", t);
			httpResponse = buildDefaultResponse(t.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
		} finally {
			httpRequest.content().release();
		}
		sendResponse(ctx, httpResponse);
	}

	protected FullHttpResponse buildDefaultResponse(String msg, HttpResponseStatus status) {
		return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer(msg.getBytes()));
	}

	private void sendResponse(ChannelHandlerContext ctx, FullHttpResponse httpResponse) {
		boolean close = false;
		try {
			ctx.write(httpResponse);
			ctx.flush();
		} catch (Exception e) {
			log.error("NettyHttpHandler write response fail.", e);
			close = true;
		} finally {
			if (close || httpResponse == null || !HttpHeaderValues.KEEP_ALIVE.toString().equals(httpResponse.headers().get(HttpHeaderNames.CONNECTION))) {
				ctx.close();
			}
		}
	}

}
