package com.kkd.myweb.domain.dynamodb.core.service;

import java.util.List;
import java.util.Optional;

import com.kkd.myweb.domain.dynamodb.core.entities.DynamoComposeEntity;
import com.kkd.myweb.domain.dynamodb.core.repositoires.DynamoComposeRepository;

public interface DynamoComposeService<E extends DynamoComposeEntity<S>, S, R extends DynamoComposeRepository<E, S>> {
	public R getRepository();

	public Optional<E> find(String pk, S sk);

	public Optional<E> findWithoutCache(String pk, S sk);

	public E save(E item);

	public E saveIfNotExists(E item);

	public void saveAll(List<E> items);

	public E delete(String pk, S sk);

	//no cache
	public Optional<E> searchOne(String pk, S sk);

	public List<E> search(String pk, List<S> skList);

	public List<E> searchGreaterThanOrEqualTo(String pk, S sk);

	public List<E> findBetween(String pk, S fromSk, S toSk);

	public long countByIndex(String indexName, String indexKey);

}
