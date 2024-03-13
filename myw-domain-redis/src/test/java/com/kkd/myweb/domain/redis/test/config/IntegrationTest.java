package com.kkd.myweb.domain.redis.test.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.kkd.myweb.SpringBootRedisTestApplication;

@ActiveProfiles("local")
@TestPropertySource(value = { "classpath:application-domain-redis-test.yml" }, factory = YamlPropertySourceFactory.class)
@ContextConfiguration(classes=SpringBootRedisTestApplication.class)
@SpringBootTest
public class IntegrationTest {

}
