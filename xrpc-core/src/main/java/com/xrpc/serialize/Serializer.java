package com.xrpc.serialize;

import com.xrpc.common.extension.SPI;
import com.xrpc.exception.SerializationException;

@SPI(value = "protostuff", isSingleton = true)
public interface Serializer {

	<T> byte[] serialize(T obj) throws SerializationException;

	<T> T deserialize(byte[] bytes, Class<T> cls) throws SerializationException;

}
