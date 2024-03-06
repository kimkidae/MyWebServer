package com.kkd.myweb.domain.dynamodb.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kkd.myweb.common.enums.Platform;
import com.kkd.myweb.domain.dynamodb.entities.UserAccount;
import com.kkd.myweb.domain.dynamodb.services.UserAccountService;

@SpringBootTest
public class UserAccountServiceTest {
	@Autowired
	private UserAccountService userAccountService;

	@Test
	public void UserAccountTest() {
		UserAccount userAccount = new UserAccount();
		userAccount.setPk("pk1");
		userAccount.setPlatform(Platform.UUID);

		userAccountService.save(userAccount);
		
	}
}
