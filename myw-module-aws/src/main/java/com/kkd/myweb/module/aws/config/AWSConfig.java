package com.kkd.myweb.module.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Configuration
public class AWSConfig {
	@Value("${amazon.accesskey:#{null}}")
	private String accessKey;

	@Value("${amazon.secretkey:#{null}}")
	private String secretKey;

    @Profile("local")
    @Bean("AwsCredentialsProvider")
    AwsCredentialsProvider localAwsCredentialsProvider() {
		return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
	}

    @Profile("!local")
    @Bean("AwsCredentialsProvider")
    AwsCredentialsProvider awsCredentialsProvider() {
		return DefaultCredentialsProvider.create();
	}

}
