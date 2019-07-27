package com.xrpc.rpc.http.netty.server;

import com.xrpc.config.ServerConfig;
import com.xrpc.exception.ServerException;
import com.xrpc.rpc.http.server.AbstractHttpServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHttpServer extends AbstractHttpServer {

	private static final int maxContentLength = 10 * 1024 * 1024;

	private Channel channel;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public NettyHttpServer(ServerConfig serverConfig) {
		super(serverConfig);
	}

	@Override
	public void start() {
		try {
			bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyHttpServerBoss", true));
			workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2, new DefaultThreadFactory("NettyHttpServerWorker", true));

			// @formatter:off
			NettyHttpServerHandler nettyHttpRequestHandler = new NettyHttpServerHandler(this);
			ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("decoder", new HttpRequestDecoder());
							ch.pipeline().addLast("aggregator", new HttpObjectAggregator(maxContentLength));
							ch.pipeline().addLast("encoder", new HttpResponseEncoder());
							ch.pipeline().addLast("chunked", new ChunkedWriteHandler());
							ch.pipeline().addLast("handler", nettyHttpRequestHandler);
						}
				})
				.childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
				.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
				.childOption(ChannelOption.SO_BACKLOG, 128);
			// @formatter:on
			ChannelFuture channelFuture = bootstrap.bind(serverConfig.getPort()).sync();
			channel = channelFuture.channel();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServerException("Failed to start netty server on localhost:" + serverConfig.getPort(), e);
		}
	}

	@Override
	public void stop() {
		if (channel != null) {
			channel.close();
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			workerGroup = null;
			bossGroup = null;
		}
	}
}
