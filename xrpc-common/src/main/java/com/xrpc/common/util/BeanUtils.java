package com.xrpc.common.util;

import java.lang.reflect.Constructor;

public final class BeanUtils {

	public static <T> T newInstance(Class<T> clazz) {
		try {
			if (clazz == null) {
				return null;
			}
			return clazz.newInstance();
		} catch (Throwable t) {
			throw new IllegalStateException("Bean instance(class: " + clazz + ")  could not be instantiated): " + t.getMessage(), t);
		}
	}

	public static <T> T newInstance(Class<T> clazz, Class<?>[] constructorTypes, Object[] constructorArgs) {
		try {
			if (clazz == null) {
				return null;
			}
			if (ArrayUtils.isEmpty(constructorTypes)) {
				return clazz.newInstance();
			}

			Constructor<T> constructor = clazz.getConstructor(constructorTypes);
			return constructor.newInstance(constructorArgs);
		} catch (Throwable t) {
			throw new IllegalStateException("Bean instance(class: " + clazz + ")  could not be instantiated): " + t.getMessage(), t);
		}
	}
}
