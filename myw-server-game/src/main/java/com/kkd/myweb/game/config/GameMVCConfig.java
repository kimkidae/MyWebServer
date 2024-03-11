package com.kkd.myweb.game.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kkd.myweb.game.http.threadlocal.ThreadLocalInterceptor;

// @ComponentScan("controllers")
@Configuration
public class GameMVCConfig implements WebMvcConfigurer {
	@Autowired
	@NonNull private ThreadLocalInterceptor threadLocalInterceptor;

	@Override
	public void addInterceptors(@NonNull InterceptorRegistry registry) {
		registry.addInterceptor(threadLocalInterceptor).addPathPatterns("/**");
	}
	
	@Bean
	CharacterEncodingFilter characterEncodingFilter() {
		return new CharacterEncodingFilter("UTF-8", true);
	}

}
