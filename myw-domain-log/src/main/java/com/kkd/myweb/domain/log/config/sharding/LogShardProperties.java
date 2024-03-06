package com.kkd.myweb.domain.log.config.sharding;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix="spring.datasource")
public class LogShardProperties {
	private String prefix;
	private List<ShardDBGroup> logs;
}
