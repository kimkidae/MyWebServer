package com.kkd.myweb.domain.log.config.sharding;

import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShardDataSource {
	private String name;
	private HikariDataSource hikari;
}
