package com.kkd.myweb.domain.dynamodb.config;

import java.net.URI;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@DependsOn("AwsCredentialsProvider")
public class DynamoDBConfig {

	@Value("${amazon.dynamodb.endpoint}")
	private String endpoint;

	@Value("${amazon.dynamodb.region}")
	private String region;

	@Autowired
	private AwsCredentialsProvider awsCredentialsProvider;

    @Bean
    DynamoDbClient dynamoDbClient() {
		return DynamoDbClient.builder()
		.credentialsProvider(awsCredentialsProvider)
		.endpointOverride(URI.create(endpoint))
        .region(Region.of(region))
		.overrideConfiguration(ClientOverrideConfiguration.builder()
				.apiCallTimeout(Duration.ofSeconds(30))
				.apiCallAttemptTimeout(Duration.ofSeconds(10))
				.build())
        .build();
	}

    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
		return DynamoDbEnhancedClient.builder()
		        .dynamoDbClient(dynamoDbClient)
		        .build();
	}
}
