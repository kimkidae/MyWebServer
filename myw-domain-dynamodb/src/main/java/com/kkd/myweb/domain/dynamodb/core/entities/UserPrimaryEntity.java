package com.kkd.myweb.domain.dynamodb.core.entities;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

public abstract class UserPrimaryEntity<S> implements DynamoEntity{
	protected String pk;

	@Override
	@DynamoDbPartitionKey
	public String getPk() {
		return pk;
	}

}
