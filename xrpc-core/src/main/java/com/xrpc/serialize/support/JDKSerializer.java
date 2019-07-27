package com.xrpc.serialize.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.xrpc.common.Constants;
import com.xrpc.exception.SerializationException;
import com.xrpc.serialize.Serializer;

public class JDKSerializer implements Serializer {

	@Override
	public byte[] serialize(Object object) throws SerializationException {
		if (object == null) {
			return Constants.EMPTY_ARRAY;
		}
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.flush();
			return byteArrayOutputStream.toByteArray();
		} catch (Throwable e) {
			throw new SerializationException("Could not write object: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
		return (T) deserialize(bytes);
	}

	private Object deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return objectInputStream.readObject();
		} catch (Throwable e) {
			throw new SerializationException("Could not read object: " + e.getMessage(), e);
		}
	}
}
