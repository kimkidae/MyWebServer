package com.kkd.myweb.domain.dynamodb.core.cache;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;

import com.kkd.myweb.domain.dynamodb.core.entities.DynamoEntity;

import software.amazon.awssdk.enhanced.dynamodb.Key;

public class DynamoCachePrimaryDao<E extends DynamoEntity> extends DynamoCacheDao<E>{
	public DynamoCachePrimaryDao(Class<E> clazz, String pkPrefix) {
		super(clazz, pkPrefix);
	}

	@Override
    protected void writeCache(Key key, byte[] cacheValue) {
		if(cacheValue == null) throw new IllegalArgumentException();
		redisService.runAndSet(getPk(key), cacheValue, cacheTtlMinutes, TimeUnit.MINUTES);
	}

	@Override
    protected byte[] readCache(Key key) {
    	String pk = getPk(key);

    	byte[] cacheValue = redisService.runAndGet(pk);
		if(!ArrayUtils.isEmpty(cacheValue)) {
			redisService.runAndExpire(pk, cacheTtlMinutes, TimeUnit.MINUTES);
		}
		return cacheValue;
	}

	@Override
	protected void deleteCache(Key key) {
		redisService.runAndDelete(getPk(key));
	}
}