package com.kkd.myweb.domain.dynamodb.core.entities;

import io.protostuff.Tag;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

public abstract class UserComposeEntity<S> implements DynamoComposeEntity<S> {
	@Setter
	@Tag(1) protected String uid;

	@DynamoDbIgnore
	public String getUid() {
		return uid;
	}

	@Override
	@DynamoDbPartitionKey
	public String getPk() {
		return uid;
	}

	@Override
	public void setPk(String pk) {
		this.uid = pk;
	}
	
	public abstract S getSk();
	public abstract void setSk(S sk);
}
