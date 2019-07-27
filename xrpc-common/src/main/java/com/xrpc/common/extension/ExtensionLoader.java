package com.xrpc.common.extension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xrpc.common.Constants;
import com.xrpc.common.util.BeanUtils;
import com.xrpc.common.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtensionLoader<T> {
	private static final String spiPathPrefix = "META-INF/xrpc/internal/";
	private static final Map<Class<?>, ExtensionLoader<?>> extensionLoaderCache = new ConcurrentHashMap<>();

	private final Class<T> type;
	private final Map<String, Class<T>> extensionClassCache = new ConcurrentHashMap<>();
	private final Map<String, T> singletonInstanceCache = new ConcurrentHashMap<>();
	private String defaultName;
	private boolean isSingleton;
	private volatile boolean loadExtensionClasses = false;

	public ExtensionLoader(Class<T> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
		ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) extensionLoaderCache.get(type);
		if (extensionLoader == null) {
			synchronized (extensionLoaderCache) {
				extensionLoader = (ExtensionLoader<T>) extensionLoaderCache.get(type);
				if (extensionLoader == null) {
					extensionLoaderCache.putIfAbsent(type, createExtensionLoader(type));
					extensionLoader = (ExtensionLoader<T>) extensionLoaderCache.get(type);
				}
			}
		}
		return extensionLoader;
	}

	private static <T> ExtensionLoader<T> createExtensionLoader(Class<T> type) {
		if (type == null) {
			throw new IllegalArgumentException("Extension type is null");
		}
		if (!type.isInterface()) {
			throw new IllegalArgumentException("Extension type(" + type + ") is not interface");
		}
		if (!type.isAnnotationPresent(SPI.class)) {
			throw new IllegalArgumentException("Extension type(" + type + ") without @SPI annotation");
		}
		ExtensionLoader<T> extensionLoader = new ExtensionLoader<>(type);
		SPI spi = type.getAnnotation(SPI.class);
		extensionLoader.defaultName = spi.value();
		extensionLoader.isSingleton = spi.isSingleton();
		return extensionLoader;
	}

	public T getDefaultExtension() {
		return this.getExtension(defaultName);
	}

	public T getExtension(String name) {
		if (name == null) {
			return null;
		}

		if (isSingleton) {
			return getSingletonInstance(name);
		} else {
			return getPrototypeInstance(name);
		}
	}

	private T getSingletonInstance(String name) {
		T instance = singletonInstanceCache.get(name);
		if (instance == null) {
			synchronized (singletonInstanceCache) {
				instance = singletonInstanceCache.get(name);
				if (instance == null) {
					T extension = createExtension(name);
					singletonInstanceCache.putIfAbsent(name, extension);
					instance = singletonInstanceCache.get(name);
				}
			}
		}
		return instance;
	}

	private T getPrototypeInstance(String name) {
		return createExtension(name);
	}

	private T createExtension(String name) {
		Class<T> extensionClass = getExtensionClass(name);
		return BeanUtils.newInstance(extensionClass);
	}

	private Class<T> getExtensionClass(String name) {
		return getExtensionClasses().get(name);
	}

	private Map<String, Class<T>> getExtensionClasses() {
		if (loadExtensionClasses) {
			return extensionClassCache;
		}

		synchronized (extensionClassCache) {
			if (loadExtensionClasses) {
				return extensionClassCache;
			}
			loadExtensionClasses();
			loadExtensionClasses = true;
		}
		return extensionClassCache;
	}

	private void loadExtensionClasses() {
		String fileName = StringUtils.join(spiPathPrefix, type.getName());
		try {
			Enumeration<URL> urls;
			ClassLoader classLoader = getClassLoader();
			if (classLoader != null) {
				urls = classLoader.getResources(fileName);
			} else {
				urls = ClassLoader.getSystemResources(fileName);
			}

			if (urls != null) {
				while (urls.hasMoreElements()) {
					loadResource(urls.nextElement());
				}
			}
		} catch (Throwable t) {
			log.error("Exception when load extension class(interface: " + type + ", description file: " + fileName + ").", t);
		}
	}

	private void loadResource(URL url) {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), Constants.DEFAULT_CHARSET))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				final int ci = line.indexOf(Constants.CHAR_SHARP);
				if (ci >= 0) {
					line.substring(0, ci);
				}
				line = line.trim();

				if (line.length() > 0) {
					try {
						String name = null, className = null;
						int i = line.indexOf(Constants.CHAR_EQUALS);
						if (i > 0) {
							name = line.substring(0, i).trim();
							className = line.substring(i + 1).trim();
						}
						if (className.length() > 0) {
							loadClass(name, className);
						}
					} catch (Throwable t) {
						log.error("Failed to load extension class(interface: " + type + ", class line: " + line, ") in " + url + ", cause: " + t.getMessage(), t);
					}
				}
			}
		} catch (Throwable t) {
			log.error("Exception when load extension class(interface: " + type + ", class file: " + url + ") in " + url, t);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadClass(String name, String className) throws Throwable {
		Class<T> clazz;
		ClassLoader classLoader = getClassLoader();
		if (classLoader == null) {
			clazz = (Class<T>) Class.forName(className);
		} else {
			clazz = (Class<T>) Class.forName(className, true, classLoader);
		}
		if (!type.isAssignableFrom(clazz)) {
			throw new IllegalStateException("Error when load extension class(interface: " + type + "+ class line: " + clazz.getName() + "), class " + clazz.getName() + "is not subtype of interface.");
		}

		if (extensionClassCache.containsKey(name)) {
			throw new IllegalStateException("Duplicate extension " + type.getName() + " name " + name + " on " + className + " and " + clazz.getName());
		}
		extensionClassCache.put(name, clazz);
	}

	private ClassLoader getClassLoader() {
		return ExtensionLoader.class.getClassLoader();
	}

}
