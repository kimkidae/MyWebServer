package com.kkd.myweb.domain.redis.config;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.lang.NonNull;

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

	@Autowired
	@NonNull private PrefixedStringKeySerializer prefixedSpKeySerializer;

	@Bean
    RedisConnectionFactory redisConnectionFactory() {
		if (isCluster)
			return clusterFactory();
		else
			return singleFactory();
	}

	private RedisConnectionFactory clusterFactory() {
		ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
				.closeStaleConnections(true)
				.enableAllAdaptiveRefreshTriggers()
				.build();

		ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
				.autoReconnect(true)
				.topologyRefreshOptions(clusterTopologyRefreshOptions)
				.validateClusterNodeMembership(false)
				.build();

		var nodes = Collections.singletonList(redisHost + ":" + redisPort);
		var readForm = ReadFrom.REPLICA_PREFERRED;
		var timeout = Duration.ofMillis(commandTimeout);

		Objects.requireNonNull(nodes);
		Objects.requireNonNull(readForm);
		Objects.requireNonNull(timeout);
		Objects.requireNonNull(clusterClientOptions);

		LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
				.readFrom(readForm)
				.commandTimeout(timeout)
				.clientOptions(clusterClientOptions)
				.build();

		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(nodes);

		return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
	}

	private RedisConnectionFactory singleFactory() {
		var timeout = Duration.ofMillis(commandTimeout);
		var host = redisHost;
		if(timeout == null || host == null) throw new IllegalArgumentException();

		LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
			.commandTimeout(timeout)
			.build();

		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, redisPort);
		return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
	}

    @Bean(name = "byteRedisTemplate")
    RedisTemplate<String, byte[]> byteRedisTemplate() {
		RedisTemplate<String, byte[]> template = new RedisTemplate<String, byte[]>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setKeySerializer(prefixedSpKeySerializer);
		template.setValueSerializer(RedisSerializer.byteArray());
		template.setHashKeySerializer(RedisSerializer.string());
		template.setHashValueSerializer(RedisSerializer.byteArray());
		return template;
	}
}