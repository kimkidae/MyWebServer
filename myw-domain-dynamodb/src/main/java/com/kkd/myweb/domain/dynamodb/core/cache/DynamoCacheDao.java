package com.kkd.myweb.domain.dynamodb.core.cache;

import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.Assert;

import com.kkd.myweb.domain.dynamodb.core.entities.DynamoEntity;
import com.kkd.myweb.domain.redis.service.RedisService;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Slf4j
public abstract class DynamoCacheDao<E extends DynamoEntity> {
	@Value("${amazon.dynamodb.cache-ttl-minutes:#{10}}")
	protected int cacheTtlMinutes;

	@Autowired
	protected RedisService redisService;

	protected final Class<E> clazz;
	protected final String pkPrefix;
	protected final ProtostuffSerializer<E> serializer;

	public DynamoCacheDao(Class<E> clazz, String pkPrefix) {
		this.clazz = clazz;
		this.pkPrefix = pkPrefix;
		this.serializer = new ProtostuffSerializer<>(clazz);
	}
	
    protected String getPk(Key key) {
		return pkPrefix + key.partitionKeyValue().s();
	}

	public void setValue(Key key, E value) {
    	assertParams(key);
    	if(value == null) {
    		log.warn("value is null. entity:{}, pk:{}, sk:{}", clazz.getSimpleName(),  key.partitionKeyValue().s(), key.sortKeyValue().isEmpty() ? "" : key.sortKeyValue().get().s());
    		return;
    	}
    	byte[] bytes = serializer.serialize(value);
    	writeCache(key, bytes);
	}

    protected abstract void writeCache(Key key, byte[] bytes);

	public Optional<E> getValue(Key key) {
    	assertParams(key);

    	byte[] bytes = readCache(key);
		if(ArrayUtils.isEmpty(bytes))
			return Optional.empty();

    	return serializer.deserialize(bytes);
	}

    protected abstract byte[] readCache(Key key);

	public void deleteValue(Key key) {
		assertParams(key);
		deleteCache(key);
	}

	protected abstract void deleteCache(Key key);

	@Retryable(value = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 3000))
	public void deleteAndRetry(String partitionKey) {
		Key key = Key.builder().partitionValue(partitionKey).build();
		redisService.delete(getPk(key));
	}

	@Recover
	public void recover(Exception e, Key key) {
		throw new RuntimeException(e.getMessage());
	}

    protected void assertParams(Key key) {
    	Assert.notNull(key, "cache key is null");
	}

}