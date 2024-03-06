package com.kkd.myweb.game.http.threadlocal;

import com.kkd.myweb.common.utils.DateTimeUtil;

public class NowTimeHolder {
	private static final ThreadLocal<Long> NOW_TIME_MILLII = new ThreadLocal<>();

	public static void set(long currentTimeMilli) {
		NOW_TIME_MILLII.set(currentTimeMilli);
	}

	public static long get() {
		if(NOW_TIME_MILLII.get() == null) NOW_TIME_MILLII.set(DateTimeUtil.nowMilli());
		return NOW_TIME_MILLII.get().longValue();
	}

	public static void clear() {
		NOW_TIME_MILLII.remove();
	}

}
