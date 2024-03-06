package com.kkd.myweb.domain.dynamodb.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kkd.myweb.common.enums.Platform;
import com.kkd.myweb.domain.dynamodb.core.repositoires.DynamoComposeRepository;
import com.kkd.myweb.domain.dynamodb.entities.UserAccount;
import com.kkd.myweb.domain.dynamodb.enums.DynamoConst;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@Repository
public class UserAccountRepository extends DynamoComposeRepository<UserAccount, String>{

	public Optional<String> findUidByPlatformId(String platformId, Platform platform) {
		var indexTable = table.index(DynamoConst.GSI_PLATFORM_ID);

		QueryConditional queryConditional = QueryConditional
			.keyEqualTo(getKey(platformId, platform.name()));

		SdkIterable<Page<UserAccount>> results = indexTable.query(
                QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build());

		for(Page<UserAccount> page : results) {
			var accountOpt = page.items().stream().findAny();
			if(accountOpt.isPresent()) {
				return Optional.of(accountOpt.get().getPk());
			}
		}
		return Optional.empty();
	}

	public Optional<UserAccount> findByPlatformId(String platformId, Platform platform) {
		var indexTable = table.index(DynamoConst.GSI_PLATFORM_ID);

		QueryConditional queryConditional = QueryConditional
			.keyEqualTo(getKey(platformId, platform.name()));

		SdkIterable<Page<UserAccount>> results = indexTable.query(
                QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build());

		for(Page<UserAccount> page : results) {
			var accountOpt = page.items().stream().findAny();
			if(accountOpt.isPresent()) {
				return Optional.of(accountOpt.get());
			}
		}
		return Optional.empty();
	}

}
