package com.kkd.myweb.domain.dynamodb.test.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kkd.myweb.common.enums.Platform;
import com.kkd.myweb.domain.dynamodb.entities.UserAccount;
import com.kkd.myweb.domain.dynamodb.services.UserAccountService;
import com.kkd.myweb.domain.dynamodb.test.config.IntegrationTest;

public class UserAccountServiceTest extends IntegrationTest{
	@Autowired
	private UserAccountService userAccountService;

	@Test
	public void UserAccountTest() {
		UserAccount userAccount = new UserAccount();
		userAccount.setUid(UUID.randomUUID().toString());
		userAccount.setPlatform(Platform.UUID);

		var savedUserAccount = userAccountService.save(userAccount);

		assertThat(userAccount).isEqualTo(savedUserAccount);
	}
}
