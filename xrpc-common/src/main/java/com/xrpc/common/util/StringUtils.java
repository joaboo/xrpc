package com.xrpc.common.util;

import java.util.ArrayList;
import java.util.List;

public final class StringUtils {
	public static final String EMPTY = "";
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static boolean isNotEmpty(final CharSequence cs) {
		return !isEmpty(cs);
	}

	public static String defaultValue(String str) {
		return str == null ? EMPTY : str;
	}

	public static String defaultValue(String str, String defaultStr) {
		return str == null ? defaultStr : str;
	}

	public static String defaultIfEmpty(String str, String defaultStr) {
		return isEmpty(str) ? defaultStr : str;
	}

	@SafeVarargs
	public static <T> String join(final T... elements) {
		return join(elements, null);
	}

	public static String join(final Object[] array, final String separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	public static String join(final Object[] array, String separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		if (separator == null) {
			separator = EMPTY;
		}

		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}

		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	public static String[] split(String str, char separatorChar) {
		return splitWorker(str, separatorChar, false);
	}

	private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}
		List<String> list = new ArrayList<String>();
		int i = 0, start = 0;
		boolean match = false;
		boolean lastMatch = false;
		while (i < len) {
			if (str.charAt(i) == separatorChar) {
				if (match || preserveAllTokens) {
					list.add(str.substring(start, i));
					match = false;
					lastMatch = true;
				}
				start = ++i;
				continue;
			}
			lastMatch = false;
			match = true;
			i++;
		}
		if (match || preserveAllTokens && lastMatch) {
			list.add(str.substring(start, i));
		}
		return list.toArray(new String[list.size()]);
	}
}
