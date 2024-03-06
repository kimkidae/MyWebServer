package com.kkd.myweb.game.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kkd.myweb.game.http.threadlocal.ThreadLocalInterceptor;

@Configuration
public class GameMVCConfig implements WebMvcConfigurer {
	@Autowired
	private ThreadLocalInterceptor threadLocalInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(threadLocalInterceptor).addPathPatterns("/**");
	}

	@Bean
	CharacterEncodingFilter characterEncodingFilter() {
		return new CharacterEncodingFilter("UTF-8", true);
	}

}
