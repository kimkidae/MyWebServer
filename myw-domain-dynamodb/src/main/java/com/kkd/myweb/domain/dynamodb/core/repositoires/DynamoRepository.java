package com.kkd.myweb.domain.dynamodb.core.repositoires;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import com.kkd.myweb.domain.dynamodb.core.cache.DynamoCacheDao;
import com.kkd.myweb.domain.dynamodb.core.entities.DynamoEntity;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableMetadata;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedLocalSecondaryIndex;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

@Slf4j
public abstract class DynamoRepository <E extends DynamoEntity> {
	@Value("${amazon.dynamodb.table-name-prefix}")
	private String tableNamePrefix;

	@Autowired
	protected DynamoDbClient client;
	@Autowired
	protected DynamoDbEnhancedClient enhanceClient;

	@Getter
	private String tableName;
	protected DynamoDbTable<E> table;
	protected final Class<E> clazz;
	protected DynamoCacheDao<E> dynamoCacheDao;

	@SuppressWarnings("unchecked")
	public DynamoRepository(DynamoCacheDao<E> dynamoCacheDao) {
		String className = ((ParameterizedType)(getClass().getGenericSuperclass())).getActualTypeArguments()[0].getTypeName();
		Class<E> clazz;
		try {
			clazz = (Class<E>)Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		this.clazz = clazz;
		this.dynamoCacheDao = dynamoCacheDao;
	}

	@PostConstruct
	public void init() {
		tableName = tableNamePrefix + clazz.getSimpleName();
		table = enhanceClient.table(tableName, BeanTableSchema.create(clazz));
		createTable();
	}

	protected abstract Key getKey(E entity);

	protected boolean exists(Key key) {
		if(dynamoCacheDao != null) {
			var cacheItem = dynamoCacheDao.getValue(key);
			if(cacheItem.isPresent()) 
				return true;
		}

		QueryEnhancedRequest request = QueryEnhancedRequest.builder()
				.limit(1)
				.attributesToProject(key.partitionKeyValue().s())
				.queryConditional(QueryConditional.keyEqualTo(key))
				.build();

		return table.query(request).items().iterator().hasNext();
	}

	protected Optional<E> find(Key key) {
		if(dynamoCacheDao != null) {
			var cacheItem = dynamoCacheDao.getValue(key);
			if(cacheItem.isPresent()) 
				return cacheItem;
		}

		var dynamodbItemOpt = findWithoutCache(key);
		if(dynamodbItemOpt.isEmpty()) return dynamodbItemOpt;

		if(dynamoCacheDao != null) dynamoCacheDao.setValue(key, dynamodbItemOpt.get());

		return dynamodbItemOpt;
	}

	protected Optional<E> findWithoutCache(Key key) {
		E dynamodbItem = table.getItem(r -> r.key(key));
		if(dynamodbItem == null)
			return Optional.empty();

		return Optional.of(dynamodbItem);
	}

	public abstract E saveIfNotExists(E entity);

	public E save(E entity) {
		table.putItem(entity);
		if(dynamoCacheDao != null) dynamoCacheDao.setValue(getKey(entity), entity);
		return entity;
	}

	public void saveAll(List<E> entities) {
		var writeBatch = WriteBatch.builder(clazz).mappedTableResource(table);
		
		for(E entity : entities) {
			writeBatch.addPutItem(entity);
		}
		WriteBatch batch = writeBatch.build();

		BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
				.writeBatches(batch)
				.build();

		//TODO to transaction write item
		enhanceClient.batchWriteItem(batchWriteItemEnhancedRequest);

		if(dynamoCacheDao != null) {
			for(E entity : entities) {
				dynamoCacheDao.setValue(getKey(entity), entity);
			}
		}
	}

	public E delete(Key key) {
		E entity = table.deleteItem(key);
		if(dynamoCacheDao != null) dynamoCacheDao.deleteValue(key);
		return entity;
	}

//	//TODO
//	public DResult<T> runQuery(DynamodbUpdateQueryAssembler queryAssembler) {
//		UpdateItemRequest updateQuery = queryAssembler.createUpdateQuery(getTableName());
//		if(updateQuery == null) return null;
//
//		UpdateItemResponse response = null;
//		try {
//			response = client.updateItem(updateQuery);
//			if(!response.sdkHttpResponse().isSuccessful()) {
//				log.warn("response fail:{}, updateQuery = {}", response.sdkHttpResponse().statusText(), updateQuery.toString());
//			}
//		}catch(ConditionalCheckFailedException e) {
//			return new DResult<>(DCode.CONDTITION_FAIL, null);
//		}
//
//		if(ReturnValue.NONE.equals(queryAssembler.getReturnValue())) {
//			return null;
//		}
//
//		E returnValue = table.tableSchema().mapToItem(response.attributes());
//
//		if(cacheService != null) {
//			cacheService.setValue(queryAssembler.getKey(), returnValue);
//		}
//		
//		return new DResult<>(DCode.SUCCESS, returnValue);
//	}

	//////////////////////////
	/// index
	public long countByIndex(String indexName, String indexKey) {
		DynamoDbIndex<E> index = table.index(indexName);

		QueryConditional conditaion = QueryConditional.keyEqualTo(Key.builder().partitionValue(indexKey).build());

		var result = index.query(QueryEnhancedRequest.builder().queryConditional(conditaion).build());

		return result.stream().count();
	}

	public void createTable() {
		var metaData = table.tableSchema().tableMetadata();
		var globalIndices = metaData.indices().stream()
				.filter(it -> !it.partitionKey().get().name().equals(metaData.primaryPartitionKey()))
				.map(index ->
					EnhancedGlobalSecondaryIndex.builder()
						.indexName(index.name())
						.projection(Projection.builder().projectionType(ProjectionType.KEYS_ONLY).build() )
					.build()
				).collect(Collectors.toList());

		var localIndices = metaData.indices().stream()
				.filter(it -> it.partitionKey().get().name().equals(metaData.primaryPartitionKey()) 
						&& !it.name().equals(TableMetadata.primaryIndexName()) )
				.map(index ->
					EnhancedLocalSecondaryIndex.builder()
						.indexName(index.name())
						.projection(Projection.builder().projectionType(ProjectionType.KEYS_ONLY).build() )
					.build()
				).collect(Collectors.toList());

		var builder = CreateTableEnhancedRequest.builder();

		if(!CollectionUtils.isEmpty(globalIndices)) {
			builder.globalSecondaryIndices(globalIndices);
		}
		if(!CollectionUtils.isEmpty(localIndices)) {
			builder.localSecondaryIndices(localIndices);
		}
		var requset = builder.build();

		try {
			table.createTable(requset);
			log.debug("table created. {}", table.tableName());
		}catch(ResourceInUseException e) {
			log.debug("table already exists. {}", table.tableName());
		}
	}

	public void deleteTable() {
		table.deleteTable();
	}

}
