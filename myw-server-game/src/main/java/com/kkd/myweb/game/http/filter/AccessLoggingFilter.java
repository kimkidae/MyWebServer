package com.kkd.myweb.game.http.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(0)
@Component
public class AccessLoggingFilter extends OncePerRequestFilter{
	
	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
		return false;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {

		var requestWrapper = new ContentCachingRequestWrapper(request);
		var responseWrapper = new ContentCachingResponseWrapper(response);

		MDC.put("servletPath", requestWrapper.getServletPath());
		filterChain.doFilter(requestWrapper, responseWrapper);

		writeAccessLog(requestWrapper, responseWrapper);

		responseWrapper.copyBodyToResponse();
	}

	private void writeAccessLog(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper) {
		String requestBody = getContentString(requestWrapper.getContentAsByteArray());
		String responseBody = getContentString(responseWrapper.getContentAsByteArray());

		if(responseWrapper.getStatus() == HttpStatus.OK.value()) {
			log.debug("[{}] [{}]", requestBody, responseBody);
		}else {
			log.error("[{}] [{}]", requestBody, responseBody);
		}
	}

	private String getContentString(byte[] contentArray) {
		if(contentArray == null) return "";
		return new String(contentArray, StandardCharsets.UTF_8);
	}
	
}
