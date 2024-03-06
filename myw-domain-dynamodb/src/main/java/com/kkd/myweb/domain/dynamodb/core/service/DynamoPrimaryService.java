package com.kkd.myweb.domain.dynamodb.core.service;

import java.util.Optional;

import com.kkd.myweb.domain.dynamodb.core.entities.DynamoEntity;
import com.kkd.myweb.domain.dynamodb.core.repositoires.DynamoPrimaryRepository;

public interface DynamoPrimaryService <E extends DynamoEntity, R extends DynamoPrimaryRepository<E>> {

	public R getRepository();

	public Optional<E> find(String pk);

	public E save(E item);

	public E saveIfNotExists(E item);

	public E delete(String pk);
}
