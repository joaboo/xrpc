package com.xrpc.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetUtils {

	public static final String LOCALHOST = "127.0.0.1";
	public static final String ANYHOST = "0.0.0.0";
	private static final int MIN_PORT = 0;
	private static final int MAX_PORT = 65535;
	private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");
	private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
	private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

	private static volatile InetAddress LOCAL_ADDRESS = null;

	public static String getLocalHost() {
		InetAddress address = getLocalAddress();
		return address == null ? LOCALHOST : address.getHostAddress();
	}

	public static InetAddress getLocalAddress() {
		if (LOCAL_ADDRESS != null)
			return LOCAL_ADDRESS;
		InetAddress localAddress = getLocalAddress0();
		LOCAL_ADDRESS = localAddress;
		return localAddress;
	}

	private static InetAddress getLocalAddress0() {
		InetAddress localAddress = null;
		try {
			localAddress = InetAddress.getLocalHost();
			if (isValidAddress(localAddress)) {
				return localAddress;
			}
		} catch (Throwable e) {
			log.warn("Failed to retrieving ip address, " + e.getMessage(), e);
		}
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces != null) {
				while (interfaces.hasMoreElements()) {
					try {
						NetworkInterface network = interfaces.nextElement();
						Enumeration<InetAddress> addresses = network.getInetAddresses();
						if (addresses != null) {
							while (addresses.hasMoreElements()) {
								try {
									InetAddress address = addresses.nextElement();
									if (isValidAddress(address)) {
										return address;
									}
								} catch (Throwable e) {
									log.warn("Failed to retrieving ip address, " + e.getMessage(), e);
								}
							}
						}
					} catch (Throwable e) {
						log.warn("Failed to retrieving ip address, " + e.getMessage(), e);
					}
				}
			}
		} catch (Throwable e) {
			log.warn("Failed to retrieving ip address, " + e.getMessage(), e);
		}
		log.error("Could not get local host ip address, will use 127.0.0.1 instead.");
		return localAddress;
	}

	public static boolean isInvalidPort(int port) {
		return port <= MIN_PORT || port > MAX_PORT;
	}

	public static boolean isValidAddress(String address) {
		return ADDRESS_PATTERN.matcher(address).matches();
	}

	public static boolean isLocalHost(String host) {
		return host != null && (LOCAL_IP_PATTERN.matcher(host).matches() || host.equalsIgnoreCase("localhost"));
	}

	public static boolean isAnyHost(String host) {
		return "0.0.0.0".equals(host);
	}

	public static boolean isInvalidLocalHost(String host) {
		return host == null || host.length() == 0 || host.equalsIgnoreCase("localhost") || host.equals("0.0.0.0") || (LOCAL_IP_PATTERN.matcher(host).matches());
	}

	public static boolean isValidLocalHost(String host) {
		return !isInvalidLocalHost(host);
	}

	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress())
			return false;
		String name = address.getHostAddress();
		return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
	}
}
