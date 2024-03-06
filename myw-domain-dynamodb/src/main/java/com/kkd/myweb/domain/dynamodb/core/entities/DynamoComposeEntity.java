package com.kkd.myweb.domain.dynamodb.core.entities;

public interface DynamoComposeEntity<S> extends DynamoEntity {
	public S getSk();
	public void setSk(S sk);

}
