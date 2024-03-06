package com.kkd.myweb.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeUtil {
	public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

	public static LocalDateTime now() {
		return LocalDateTime.now(ZONE_OFFSET);
	}

	public static LocalDate nowDate() {
		return LocalDate.now(ZONE_OFFSET);
	}

	public static long nowMilli() {
		return now().toInstant(ZONE_OFFSET).toEpochMilli();
	}

	public static long dateTimeToMilli(LocalDateTime src) {
		return src.toInstant(ZONE_OFFSET).toEpochMilli();
	}

	public static LocalDateTime milliToDateTime(long datetimeMilli) {
		return Instant.ofEpochMilli(datetimeMilli).atZone(ZONE_OFFSET).toLocalDateTime();
	}

}
