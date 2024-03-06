package com.kkd.myweb.domain.redis.config;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.Getter;

@Configuration
public class RedisConfig {
	@Getter
	@Value("${spring.data.redis.key-prefix:}")
	private String keyPrefix;

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Value("${spring.data.redis.command-timeout:#{5000}}")
	private long commandTimeout;

	@Value("${spring.data.redis.cluster-on:#{false}}")
	private boolean isCluster;

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
		if (isCluster)
			return clusterFactory();
		else
			return singleFactory();
	}

	private RedisConnectionFactory clusterFactory() {
		List<String> nodes = Collections.singletonList(redisHost + ":" + redisPort);
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(nodes);

		ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
				.closeStaleConnections(true)
				.enableAllAdaptiveRefreshTriggers()
				.build();

		ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
				.autoReconnect(true)
				.topologyRefreshOptions(topologyRefreshOptions)
				.validateClusterNodeMembership(false)
				.build();

		LettuceClientConfiguration  lettuceClientConfiguration = LettuceClientConfiguration.builder()
				.readFrom(ReadFrom.REPLICA_PREFERRED)
				.commandTimeout(Duration.ofMillis(commandTimeout))
				.clientOptions(clusterClientOptions)
				.build();

		return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
	}

	private RedisConnectionFactory singleFactory() {
		LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
				.commandTimeout(Duration.ofMillis(commandTimeout))
				.build();

		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
		return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
	}

    @Bean(name = "byteRedisTemplate")
    RedisTemplate<String, byte[]> byteRedisTemplate() {
		RedisTemplate<String, byte[]> template = new RedisTemplate<String, byte[]>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setKeySerializer(RedisSerializer.string());
		template.setValueSerializer(RedisSerializer.byteArray());
		template.setHashKeySerializer(RedisSerializer.string());
		template.setHashValueSerializer(RedisSerializer.byteArray());
		return template;
	}
}