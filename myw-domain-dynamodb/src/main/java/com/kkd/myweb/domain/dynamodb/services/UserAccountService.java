package com.kkd.myweb.domain.dynamodb.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kkd.myweb.common.enums.Platform;
import com.kkd.myweb.domain.dynamodb.core.service.DynamoComposeServiceImpl;
import com.kkd.myweb.domain.dynamodb.entities.UserAccount;
import com.kkd.myweb.domain.dynamodb.repositories.UserAccountRepository;

@Service
public class UserAccountService extends DynamoComposeServiceImpl<UserAccount, String, UserAccountRepository>{

	public Optional<String> findUidByPlatformId(String platformId, Platform platform) {
		return getRepository().findUidByPlatformId(platformId, platform);
	}

	public void dropTable() {
		getRepository().deleteTable();
	}

}
