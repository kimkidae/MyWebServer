package com.kkd.myweb.domain.log.config.sharding;

import java.util.List;

import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShardDBGroup {
	private String name;
	private HikariDataSource hikari;
	private	List<ShardDataSource> slaves;
}
