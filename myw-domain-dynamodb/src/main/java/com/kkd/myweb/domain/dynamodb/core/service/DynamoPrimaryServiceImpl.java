package com.kkd.myweb.domain.dynamodb.core.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.kkd.myweb.domain.dynamodb.core.entities.DynamoEntity;
import com.kkd.myweb.domain.dynamodb.core.repositoires.DynamoPrimaryRepository;

public class DynamoPrimaryServiceImpl<E extends DynamoEntity, R extends DynamoPrimaryRepository<E>> implements DynamoPrimaryService<E, R>{
	@Autowired
	protected R repository;

	@Override
	public R getRepository() {
		return repository;
	}

	@Override
	public Optional<E> find(String uid) {
		return repository.find(uid);
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
	public E delete(String uid) {
		return repository.delete(uid);
	}

//	//for query
//	public DynamodbUpdateQueryAssembler createQueryAssembler(String uid) {
//		Key key = repository.getKey(uid);
//		return new DynamodbUpdateQueryAssembler(key);
//	}
//
//	public DResult<T> runQuery(DynamodbUpdateQueryAssembler queryAssembler) {
//		var result = repository.runQuery(queryAssembler);
//		if(result == null) return null;
//
//		if(queryAssembler.isWriteLog()) {
//			writeQueryLogs(queryAssembler, result.getReturnValue());
//		}
//		return result;
//	}
}
