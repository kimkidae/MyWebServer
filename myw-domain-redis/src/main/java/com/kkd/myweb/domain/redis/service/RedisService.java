package com.kkd.myweb.domain.redis.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;


@Service
public class RedisService {

	@Autowired
	@Qualifier("byteRedisTemplate") 
	protected RedisTemplate<String, byte[]> byteRedisTemplate;

	public void runAndSet(@NonNull String key, @NonNull byte[] value, long timeout, @NonNull TimeUnit timeUnit) {
		run(() -> byteRedisTemplate.opsForValue().set(key, value, timeout, timeUnit));
	}

	public byte[] runAndGet(@NonNull String key) {
		return runAndGet(() -> byteRedisTemplate.opsForValue().get(key));
	}

	public void runAndExpire(@NonNull String key, long timeout, @NonNull TimeUnit timeUnit) {
		run(() -> byteRedisTemplate.expire(key, timeout, timeUnit));
	}

	public void runAndDelete(@NonNull String key) {
		run(() -> byteRedisTemplate.delete(key));
	}

	public void delete(@NonNull String key) {
		byteRedisTemplate.delete(key);
	}

	public void runAndHashPut(@NonNull String key, @NonNull Object hashKey, byte[] value) {
		if(value == null) throw new IllegalArgumentException();
		run(() -> byteRedisTemplate.opsForHash().put(key, hashKey, value));
	}

	public Object runAndHashGet(@NonNull String key, @NonNull String hashKey) {
		return runAndGet(() -> byteRedisTemplate.opsForHash().get(key, hashKey));
	}

	public void runAndHashDelete(@NonNull String key, String hashKey) {
		run(() -> byteRedisTemplate.opsForHash().delete(key, hashKey));
	}

	public void run(Runnable redisCommand) {
		redisCommand.run();
	}

	public <V> V runAndGet(Supplier<V> redisCommand) {
		return redisCommand.get();
	}

}
