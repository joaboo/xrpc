package com.xrpc.common;

public class Constants {

	public static final String DEFAULT_CHARSET = "utf-8";

	public static final String DEFAULT_VERSION = "Lastest";

	public static final char CHAR_COLON = ':';

	public static final char CHAR_SHARP = '#';

	public static final char CHAR_EQUALS = '=';

	public static final char CHAR_HYPHEN = '-';

	public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream;charset=utf-8";

	public static final String DEFAULT_THREAD_NAME = "xrpc";

	public static final int DEFAULT_CORE_THREADS = 0;

	public static final int DEFAULT_THREADS = 100;

	public static final boolean DEFAULT_KEEP_ALIVE = true;

	public static final long DEFAULT_ALIVE = 60 * 1000;

	public static final int DEFAULT_QUEUES = 0;

	public static final int DEFAULT_IO_THREADS = 20;

	public static final String HTTP_PREFIX = "http://";
	public static final String HTTPS_PREFIX = "https://";

	public static final String DEFAULT_RPC_REQUEST_PATH = "/xrpc/dispatch";
	public static final String DEFAULT_HEALTH_CHECK_PATH = "/xrpc/health";

	public static final String DEFAULT_DISPATCHER_URL_PATTERN = "/xrpc/dispatch/*";
	public static final String DEFAULT_HEALTH_URL_PATTERN = "/xrpc/health/*";

	public static final byte[] EMPTY_ARRAY = new byte[0];

	public static enum SerializationEnum {
		jdk, kryo, protostuff;
	}

	public static enum LbStrategyEnum {
		random, roundrobin;
	}

	public static enum HaStrategyEnum {
		failfast, failover;
	}
}
