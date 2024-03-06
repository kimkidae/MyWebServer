package com.kkd.myweb.domain.dynamodb.core.repositoires;

import java.util.Optional;

import com.kkd.myweb.domain.dynamodb.core.cache.DynamoCacheDao;
import com.kkd.myweb.domain.dynamodb.core.entities.DynamoEntity;

import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

public abstract class DynamoPrimaryRepository <E extends DynamoEntity> extends DynamoRepository<E> {
	public DynamoPrimaryRepository() {
		super(null);
	}

	public DynamoPrimaryRepository(DynamoCacheDao<E> dynamoCacheDao) {
		super(dynamoCacheDao);
	}

	@Override
	protected Key getKey(E item) {
		return Key.builder().partitionValue(item.getPk()).build();
	}

	public Key getKey(String pk) {
		return Key.builder().partitionValue(pk).build();
	}

	public boolean exists(String pk) {
		return exists(getKey(pk));
	}

	public Optional<E> find(String pk) {
		return find(getKey(pk));
	}

	public Optional<E> findWithoutCache(String pk) {
		return findWithoutCache(getKey(pk));
	}

	@Override
	public E saveIfNotExists(E item) {
		PutItemEnhancedRequest<E> request = PutItemEnhancedRequest.builder(clazz)
				.item(item)
				.conditionExpression(Expression.builder().expression("attribute_not_exists(pk)").build())
				.build();

		try {
			table.putItem(request);
		}catch(ConditionalCheckFailedException e) {
			return null;//already exists
		}
		if(dynamoCacheDao != null) dynamoCacheDao.setValue(getKey(item), item);
		return item;
	}

	public E delete(String pk) {
		return delete(getKey(pk));
	}
}
