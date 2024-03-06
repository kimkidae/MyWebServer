package com.kkd.myweb.domain.log.config.sharding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomShardingAlgorithm {
//	implements PreciseShardingAlgorithm<String> 
//	private final String prefix;
//	private final int shardSize;
//
//	@Override
//	public String doSharding(final Collection<String> shardNames, final PreciseShardingValue<String> shardingValue) {
//		String shardName = getLogShardName(shardingValue.getValue(), shardSize);
//
//		log.debug("shardName : {}, shardingValue : {}", shardName, shardingValue);
//
//		if (shardNames.contains(shardName)) {
//			return shardName;
//		}
//
//		throw new UnsupportedOperationException();
//	}
//
//	public String getLogShardName(String shardingValue, int shardSize) {
//		return prefix + getShardingKey(shardingValue, shardSize);
//	}
//
//	public int getShardingKey(String shardingValue, int shardSize) {
//		return (int)(Math.abs(crc32(shardingValue)) % shardSize + 1);
//	}
//
//	public long crc32(String value) {
//		CRC32 crc32 = new CRC32();
//		crc32.update(value.getBytes());
//		return crc32.getValue();
//	}
}