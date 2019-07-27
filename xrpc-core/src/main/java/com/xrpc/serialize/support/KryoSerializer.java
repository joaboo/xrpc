package com.xrpc.serialize.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xrpc.common.Constants;
import com.xrpc.exception.SerializationException;
import com.xrpc.serialize.Serializer;

public class KryoSerializer implements Serializer {

	private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		kryo.setRegistrationRequired(false);
		kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		return kryo;
	});

	@Override
	public <T> byte[] serialize(T object) throws SerializationException {
		if (object == null) {
			return Constants.EMPTY_ARRAY;
		}

		try (Output output = new Output(new ByteArrayOutputStream())) {
			Kryo kryo = kryoThreadLocal.get();
			kryo.writeClassAndObject(output, object);
			return output.toBytes();
		} catch (Exception e) {
			throw new SerializationException("Could not write object: " + e.getMessage(), e);
		}

	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		try (Input input = new Input(new ByteArrayInputStream(bytes))) {
			Kryo kryo = kryoThreadLocal.get();
			Object bean = kryo.readClassAndObject(input);
			return clazz.cast(bean);
		} catch (Throwable e) {
			throw new SerializationException("Could not read object: " + e.getMessage(), e);
		}

	}
}
