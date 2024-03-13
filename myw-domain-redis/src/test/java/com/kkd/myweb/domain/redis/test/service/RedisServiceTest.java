package com.kkd.myweb.domain.redis.test.service;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kkd.myweb.domain.redis.service.RedisService;
import com.kkd.myweb.domain.redis.test.config.IntegrationTest;

public class RedisServiceTest extends IntegrationTest{
    @Autowired
    private RedisService redisService;

    @Test
    void redisServiceTest(){
        byte[] bytes = new byte[]{0,0,1};
        redisService.runAndSet("key", bytes, 1, TimeUnit.MINUTES);
    }
}
