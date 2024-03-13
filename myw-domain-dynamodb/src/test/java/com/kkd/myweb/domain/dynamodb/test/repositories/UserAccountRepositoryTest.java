package com.kkd.myweb.domain.dynamodb.test.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kkd.myweb.common.enums.Platform;
import com.kkd.myweb.common.utils.DateTimeUtil;
import com.kkd.myweb.common.utils.Version;
import com.kkd.myweb.domain.dynamodb.entities.UserAccount;
import com.kkd.myweb.domain.dynamodb.repositories.UserAccountRepository;
import com.kkd.myweb.domain.dynamodb.test.config.IntegrationTest;

public class UserAccountRepositoryTest extends IntegrationTest{
    @Autowired
    private  UserAccountRepository userAccountRepository;

    @Test
    void UserAccountTest(){
        UserAccount userAccount = new UserAccount();
        userAccount.setUid(UUID.randomUUID().toString());//pk
        userAccount.setPlatform(Platform.UUID);//sk
        userAccount.setVersion(1);
        userAccount.setPlatformId(UUID.randomUUID().toString());
        userAccount.setClientVersion(Version.parse("0.0.1").toString());
        userAccount.setCreateDt(DateTimeUtil.now());
        userAccount.setLoginDt(userAccount.getCreateDt());

        var savedUserAccount = userAccountRepository.save(userAccount);
        assertThat(userAccount).isEqualTo(savedUserAccount);
        assertThat(userAccount.getClientVersion()).isEqualTo("0.0.1");

        var uidOptional = userAccountRepository.findUidByPlatformId(userAccount.getPlatformId(), userAccount.getPlatform());
        assertThat(uidOptional.get()).isEqualTo(userAccount.getUid());

        var userAccountByIndex = userAccountRepository.findByPlatformId(userAccount.getPlatformId(), userAccount.getPlatform()).get();

        assertThat(userAccountByIndex.getPlatform()).isNotNull();
        assertThat(userAccountByIndex.getPlatformId()).isNotNull();
        assertThat(userAccountByIndex.getUid()).isNotNull();
    }

}
