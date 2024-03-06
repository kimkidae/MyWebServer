package com.kkd.myweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class MYWGameServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MYWGameServerApplication.class, args);
	}

	@PreDestroy
	public void onDestroy() {
		log.info("MYWGameServerApplication onDestroy");
	}

}
