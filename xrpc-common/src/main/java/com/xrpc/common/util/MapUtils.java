package com.xrpc.common.util;

import java.util.Map;

public final class MapUtils {

	public static boolean isEmpty(final Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	public static boolean isNotEmpty(final Map<?, ?> map) {
		return !isEmpty(map);
	}

	public static <K, V> V getObject(final Map<? super K, V> map, final K key) {
		if (map != null) {
			return map.get(key);
		}
		return null;
	}
}
