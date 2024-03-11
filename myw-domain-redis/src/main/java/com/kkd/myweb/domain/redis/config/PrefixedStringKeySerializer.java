package com.kkd.myweb.domain.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class PrefixedStringKeySerializer extends StringRedisSerializer{
	@Value("${spring.data.redis.key-prefix:}")
	private String keyPrefix;

    @SuppressWarnings("null")
    @Override
    public byte[] serialize(@Nullable String value) {
        return super.serialize(keyPrefix + value);
    }
    
}
