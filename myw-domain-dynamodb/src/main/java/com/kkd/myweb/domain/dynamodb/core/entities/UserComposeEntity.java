package com.kkd.myweb.domain.dynamodb.core.entities;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

public abstract class UserComposeEntity<S> implements DynamoComposeEntity<S> {
	protected String uid;

	@Override
	@DynamoDbPartitionKey
	public String getPk() {
		return uid;
	}

	public abstract S getSk();
	public abstract void setSk(S sk);
}
