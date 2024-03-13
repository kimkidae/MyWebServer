package com.kkd.myweb.domain.dynamodb.test.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.kkd.myweb.SpringBootDynamodbTestApplication;

@ActiveProfiles("local")
@TestPropertySource(value = { "classpath:application-domain-dynamodb-test.yml" }, factory = YamlPropertySourceFactory.class)
@ContextConfiguration(classes=SpringBootDynamodbTestApplication.class)
@SpringBootTest
public class IntegrationTest {

}
