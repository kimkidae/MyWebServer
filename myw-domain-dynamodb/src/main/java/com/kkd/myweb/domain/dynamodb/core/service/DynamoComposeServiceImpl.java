package com.kkd.myweb.domain.dynamodb.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.kkd.myweb.domain.dynamodb.core.entities.DynamoComposeEntity;
import com.kkd.myweb.domain.dynamodb.core.repositoires.DynamoComposeRepository;

public class DynamoComposeServiceImpl<E extends DynamoComposeEntity<S>, S, R extends DynamoComposeRepository<E, S>> implements DynamoComposeService<E, S, R>{
	@Autowired
	protected R repository;

	@Override
	public R getRepository() {
		return repository;
	}

	@Override
	public Optional<E> find(String pk, S sk) {
		return repository.find(pk, sk);
	}

	@Override
	public Optional<E> findWithoutCache(String pk, S sk) {
		return repository.findWithoutCache(pk, sk);
	}

	@Override
	public E save(E item) {
		return repository.save(item);
	}

	@Override
	public E saveIfNotExists(E item) {
		return repository.saveIfNotExists(item);
	}

	@Override
	public void saveAll(List<E> items) {
		repository.saveAll(items);
	}

	@Override
	public E delete(String pk, S sk) {
		return repository.delete(pk, sk);
	}


	//no cache
	@Override
	public Optional<E> searchOne(String pk, S sk) {
		return repository.searchOneGreaterThanOrEqualTo(pk, sk);
	}

	@Override
	public List<E> search(String pk, List<S> skList) {
		return repository.search(pk, skList);
	}

	@Override
	public List<E> searchGreaterThanOrEqualTo(String pk, S sk) {
		return repository.searchGreaterThanOrEqualTo(pk, sk);
	}

	@Override
	public List<E> findBetween(String pk, S fromSk, S toSk){
		return repository.searchBetween(pk, fromSk, pk, toSk);
	}

	@Override
	public long countByIndex(String indexName, String indexKey) {
		return repository.countByIndex(indexName, indexKey);
	}

//	//for query
//	public DynamodbUpdateQueryAssembler createQueryAssembler(String uid, S sk) {
//		Key key = repository.getKey(uid, sk);
//		return new DynamodbUpdateQueryAssembler(key);
//	}
//
//	protected DResult<T> runQuery(DynamodbUpdateQueryAssembler queryAssembler) {
//		var result = repository.runQuery(queryAssembler);
//		if(result == null) return null;
//
//		if(queryAssembler.isWriteLog()) {
//			writeQueryLogs(queryAssembler, result.getReturnValue());
//		}
//		return result;
//	}

}
