package com.kkd.myweb.domain.dynamodb.entities;

import java.time.LocalDateTime;

import com.kkd.myweb.common.enums.Platform;
import com.kkd.myweb.domain.dynamodb.core.entities.UserComposeEntity;
import com.kkd.myweb.domain.dynamodb.enums.DynamoConst;

import io.protostuff.Tag;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Setter
@DynamoDbBean
public class UserAccount extends UserComposeEntity<String>{
	@Tag(2) private Platform platform;
	@Tag(3) private int version; //schema version
	@Tag(4) private String platformId;
	@Tag(5) private String clientVersion;

	@Tag(6) private LocalDateTime createDt;
	@Tag(7) private LocalDateTime preLoginDt;
	@Tag(8) private LocalDateTime loginDt;

	@Override
	@DynamoDbSecondarySortKey(indexNames = {DynamoConst.GSI_PLATFORM_ID})
	@DynamoDbSortKey
	public String getSk() {
		return platform.name();
	}

	@Override
	public void setSk(String sk) {
		platform = Platform.valueOf(sk);
	}
	
	@DynamoDbIgnore
	public Platform getPlatform() {
		return platform;
	}

	@DynamoDbAttribute(DynamoConst.VERSION)
	public int getVersion() {
		return version;
	}

	@DynamoDbSecondaryPartitionKey(indexNames = {DynamoConst.GSI_PLATFORM_ID})
	@DynamoDbAttribute(DynamoConst.PLATFOMR_ID)
	public String getPlatformId() {
		return platformId;
	}

	@DynamoDbAttribute(DynamoConst.CLIENT_VERSION)
	public String getClientVersion() {
		return clientVersion;
	}

	@DynamoDbAttribute(DynamoConst.CREATE_DT)
	public LocalDateTime getCreateDt() {
		return createDt;
	}

	@DynamoDbAttribute(DynamoConst.PRE_LOGIN_DT)
	public LocalDateTime getPreLoginDt() {
		return preLoginDt;
	}

	@DynamoDbAttribute(DynamoConst.LOGIN_DT)
	public LocalDateTime getLoginDt() {
		return loginDt;
	}


}
