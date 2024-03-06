package com.kkd.myweb.domain.dynamodb.core.cache;

import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtostuffSerializer<E> {
	private static final byte[] EMPTY_ARRAY = new byte[0];
	private static final int BUFFER_SIZE = 4096;
	private static final ThreadLocal<LinkedBuffer> BUFFER = ThreadLocal.withInitial(() -> LinkedBuffer.allocate(BUFFER_SIZE));

	private Schema<E> schema;

	public ProtostuffSerializer(Class<E> clazz) {
		schema = RuntimeSchema.getSchema(clazz);
	}

	public byte[] serialize(E item) {
		if (item == null) 
			return EMPTY_ARRAY;

		LinkedBuffer buffer = BUFFER.get();
		byte[] bytes;
		try {
			bytes = ProtostuffIOUtil.toByteArray(item, schema, buffer);
		} finally {
			buffer.clear();
		}
		return bytes;
	}

	public Optional<E> deserialize(byte[] bytes) {
		if(ArrayUtils.isEmpty(bytes))
			return Optional.empty();

		E item = schema.newMessage();
		ProtostuffIOUtil.mergeFrom(bytes, item, schema);

		return Optional.of(item);
	}
}