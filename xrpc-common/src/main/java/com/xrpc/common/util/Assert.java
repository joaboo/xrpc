package com.xrpc.common.util;

import java.util.Collection;
import java.util.Map;

public abstract class Assert {

	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(String text, String message) {
		if (StringUtils.isEmpty(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(Object[] array, String message) {
		if (ArrayUtils.isEmpty(array)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(Collection<?> collection, String message) {
		if (CollectionUtils.isEmpty(collection)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(Map<?, ?> map, String message) {
		if (MapUtils.isEmpty(map)) {
			throw new IllegalArgumentException(message);
		}
	}

}
