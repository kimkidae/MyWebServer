package com.kkd.myweb.domain.redis.config;

import lombok.Getter;

@Getter
public class SpringDataRedisProperty {
	private String keyPrefix;

    private String host;

    private int port;

	private long commandTimeout = 5000L;

    private boolean clusterOn;
}
