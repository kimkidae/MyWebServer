package com.kkd.myweb.domain.log.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
public class LogQueryDSLConfig {

	@PersistenceContext(unitName = "logUnit")
	private EntityManager logEntityManager;

	@Bean
	JPAQueryFactory logJpaQueryFactory() {
		return new JPAQueryFactory(logEntityManager);
	}

}
