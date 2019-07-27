package com.xrpc.common.util;

public final class NumberUtils {
	public static final Long LONG_ZERO = Long.valueOf(0L);
	public static final Integer INTEGER_ZERO = Integer.valueOf(0);
	public static final Short SHORT_ZERO = Short.valueOf((short) 0);
	public static final Byte BYTE_ZERO = Byte.valueOf((byte) 0);
	public static final Double DOUBLE_ZERO = Double.valueOf(0.0d);
	public static final Float FLOAT_ZERO = Float.valueOf(0.0f);

	// return non-negative int value of originValue
	public static int nonNegative(int originValue) {
		return 0x7fffffff & originValue;
	}

	public static Integer defaultValue(Integer value) {
		return value == null ? INTEGER_ZERO : value;
	}

	public static Long defaultValue(Long value) {
		return value == null ? LONG_ZERO : value;
	}

	public static <T extends Number> T defaultValue(final T value, final T defaultValue) {
		return value != null ? value : defaultValue;
	}
}
