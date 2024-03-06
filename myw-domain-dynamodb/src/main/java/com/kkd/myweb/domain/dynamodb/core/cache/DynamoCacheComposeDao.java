package com.kkd.myweb.domain.dynamodb.core.cache;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;

import com.kkd.myweb.domain.dynamodb.core.entities.DynamoComposeEntity;

import software.amazon.awssdk.enhanced.dynamodb.Key;

public class DynamoCacheComposeDao<E extends DynamoComposeEntity<S>, S> extends DynamoCacheDao<E>{

	public DynamoCacheComposeDao(Class<E> clazz, String pkPrefix) {
		super(clazz, pkPrefix);
	}

	private String getSk(Key key) {
		if(key.sortKeyValue().get().n().isEmpty()) {
			return key.sortKeyValue().get().s();
		}else {
			return key.sortKeyValue().get().n();
		}
	}

	@Override
	protected void writeCache(Key key, byte[] cacheValue) {
		String pk = getPk(key);
		redisService.runAndHashPut(pk, getSk(key), cacheValue);
		redisService.runAndExpire(pk, cacheTtlMinutes, TimeUnit.MINUTES);
	}

	@Override
	protected byte[] readCache(Key key) {
		String pk = getPk(key);

		byte[] cacheValue = (byte[])redisService.runAndHashGet(pk, getSk(key));
		if(!ArrayUtils.isEmpty(cacheValue)) {
			redisService.runAndExpire(pk, cacheTtlMinutes, TimeUnit.MINUTES);
		}
		return cacheValue;
	}

	@Override
	protected void deleteCache(Key key) {
		redisService.runAndHashDelete(getPk(key), getSk(key));
	}
}
