package com.xrpc.serialize.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xrpc.common.Constants;
import com.xrpc.exception.SerializationException;
import com.xrpc.serialize.Serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtostuffSerializer implements Serializer {

	private final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();
	private final ThreadLocal<LinkedBuffer> bufferThreadLocal = ThreadLocal.withInitial(LinkedBuffer::allocate);

	@SuppressWarnings("unchecked")
	@Override
	public <T> byte[] serialize(T obj) throws SerializationException {
		if (obj == null) {
			return Constants.EMPTY_ARRAY;
		}

		LinkedBuffer buffer = bufferThreadLocal.get();
		try {
			Schema<T> schema = getSchema((Class<T>) obj.getClass());
			return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} catch (Throwable e) {
			throw new SerializationException("Could not write object: " + e.getMessage(), e);
		} finally {
			buffer.clear();
		}
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> cls) throws SerializationException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		try {
			Schema<T> schema = getSchema(cls);
			T message = schema.newMessage();
			ProtostuffIOUtil.mergeFrom(bytes, message, schema);
			return message;
		} catch (Throwable e) {
			throw new SerializationException("Could not read object: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Schema<T> getSchema(Class<T> cls) {
		Schema<T> schema = (Schema<T>) schemaCache.get(cls);
		if (schema == null) {
			schema = RuntimeSchema.createFrom(cls);
			if (schema != null) {
				schemaCache.put(cls, schema);
			}
		}
		return schema;
	}
}
