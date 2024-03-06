package com.kkd.myweb.domain.dynamodb.core.repositoires;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kkd.myweb.domain.dynamodb.core.cache.DynamoCacheDao;
import com.kkd.myweb.domain.dynamodb.core.entities.DynamoComposeEntity;

import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

public abstract class DynamoComposeRepository <E extends DynamoComposeEntity<S>, S> extends DynamoRepository<E>{

	public DynamoComposeRepository() {
		super(null);
	}
	public DynamoComposeRepository(DynamoCacheDao<E> dynamoCacheDao) {
		super(dynamoCacheDao);
	}

	@Override
	protected Key getKey(E item) {
		if(item.getSk() instanceof String) {
			return Key.builder().partitionValue(item.getPk()).sortValue((String)item.getSk()).build();
		}else{
			return Key.builder().partitionValue(item.getPk()).sortValue((Number)item.getSk()).build();
		}
	}

	public Key getKey(String pk, S sk) {
		if(sk instanceof String) {
			return Key.builder().partitionValue(pk).sortValue((String)sk).build();
		}else {
			return Key.builder().partitionValue(pk).sortValue((Number)sk).build();
		}
	}

	public boolean exists(String pk, S sk) {
		return exists(getKey(pk, sk));
	}

	public Optional<E> find(String pk, S sk) {
		return find(getKey(pk, sk));
	}

	public Optional<E> findWithoutCache(String pk, S sk) {
		return findWithoutCache(getKey(pk, sk));
	}

	@Override
	public E saveIfNotExists(E item) {
		PutItemEnhancedRequest<E> request = PutItemEnhancedRequest.builder(clazz)
				.item(item)
				.conditionExpression(Expression.builder().expression("attribute_not_exists(pk) AND attribute_not_exists(sk)").build())
				.build();

		try {
			table.putItem(request);
		}catch(ConditionalCheckFailedException e) {
			return null;//already exists
		}
		if(dynamoCacheDao != null) dynamoCacheDao.setValue(getKey(item), item);
		return item;
	}

	public E delete(String pk, S sk) {
		return delete(getKey(pk, sk));
	}

	// with CACHE
	//////////////////////

	
	//////////////////////
	// without CACHE 조회 항목들만 존재 해야함. 수정 항목은 cache 처리가 동반되어야 한다.

	public List<E> search(String pk, List<S> skList){
		var batchBuilder = ReadBatch.builder(clazz).mappedTableResource(table);
		for(var sk : skList) {
			batchBuilder.addGetItem(getKey(pk, sk));
		}
		var results = enhanceClient.batchGetItem(BatchGetItemEnhancedRequest.builder()
				.addReadBatch(batchBuilder.build())
				.build());

		return results.resultsForTable(table).stream().collect(Collectors.toList());
	}

	public Optional<E> searchOneGreaterThanOrEqualTo(String pk, S sk){
		return table.query(QueryEnhancedRequest.builder()
				.queryConditional(QueryConditional.sortGreaterThanOrEqualTo(getKey(pk, sk))).limit(1).build())
				.items().stream().findFirst();
	}

	public List<E> searchGreaterThanOrEqualTo(String pk, S sk){
		return table.query(QueryConditional.sortGreaterThanOrEqualTo(getKey(pk, sk))).items().stream().collect(Collectors.toList());
	}

	public List<E> searchBetween(String fromPk, S fromSk, String toPk, S toSk){
		return table.query(QueryConditional.sortBetween(getKey(fromPk, fromSk), getKey(toPk, toSk))).items().stream().collect(Collectors.toList());
	}

}
