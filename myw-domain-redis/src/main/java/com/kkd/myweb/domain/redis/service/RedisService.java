package com.kkd.myweb.domain.redis.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.kkd.myweb.domain.redis.config.RedisConfig;

@Service
public class RedisService {
	private final String keyPrefix;

	public RedisService(RedisConfig redisConfig) {
		keyPrefix = redisConfig.getKeyPrefix();
	}

	@Autowired
	@Qualifier("byteRedisTemplate") 
	protected RedisTemplate<String, byte[]> byteRedisTemplate;

	private String getRedisKey(String key) {
		return keyPrefix + ":"+ key;
	}

	public void runAndSet(String key, byte[] value, long timeout, TimeUnit timeUnit) {
    	run(() -> byteRedisTemplate.opsForValue().set(getRedisKey(key), value, timeout, timeUnit));
	}

	public byte[] runAndGet(String key) {
		return runAndGet(() -> byteRedisTemplate.opsForValue().get(getRedisKey(key)));
	}

	public void runAndExpire(String key, long timeout, TimeUnit timeUnit) {
		run(() -> byteRedisTemplate.expire(getRedisKey(key), timeout, timeUnit));
	}

	public void runAndDelete(String key) {
		run(() -> byteRedisTemplate.delete(getRedisKey(key)));
	}

	public void delete(String key) {
		byteRedisTemplate.delete(getRedisKey(key));
	}

	public void runAndHashPut(String key, Object hashKey, Object value) {
		run(() -> byteRedisTemplate.opsForHash().put(getRedisKey(key), hashKey, value));
	}

	public Object runAndHashGet(String key, String hashKey) {
		return runAndGet(() -> byteRedisTemplate.opsForHash().get(getRedisKey(key), hashKey));
	}

	public void runAndHashDelete(String key, String hashKey) {
		run(() -> byteRedisTemplate.opsForHash().delete(getRedisKey(key), hashKey));
	}

	public void run(Runnable redisCommand) {
		redisCommand.run();
	}

	public <V> V runAndGet(Supplier<V> redisCommand) {
		return redisCommand.get();
	}

}
