package com.kkd.myweb.game.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kkd.myweb.common.enums.Platform;
import com.kkd.myweb.domain.dynamodb.entities.UserAccount;
import com.kkd.myweb.domain.dynamodb.services.UserAccountService;

@SpringBootTest
public class AccountServiceTest {
	
	@Autowired
	private UserAccountService userAccountService;

	@Test
	public void createAccountTest() {
//		userAccountService.dropTable();

		UserAccount userAccount = new UserAccount();
		userAccount.setPk("pk1");
		userAccount.setPlatformId(UUID.randomUUID().toString());
		userAccount.setPlatform(Platform.UUID);

		var updateUserAccount = userAccountService.save(userAccount);

		assertThat(userAccount).isEqualTo(updateUserAccount);

		var optional = userAccountService.findUidByPlatformId(userAccount.getPlatformId(), userAccount.getPlatform());

		assertThat(optional).isPresent();
		assertThat(optional.get()).isEqualTo(userAccount.getPk());

	}

}
