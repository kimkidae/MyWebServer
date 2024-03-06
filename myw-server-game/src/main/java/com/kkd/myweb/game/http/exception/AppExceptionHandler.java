package com.kkd.myweb.game.http.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {

	@ExceptionHandler(Throwable.class)
	protected ResponseEntity<Object> handleException(Throwable throwable) {
		log.error(throwable.getClass().getName(), throwable);
		return new ResponseEntity<Object>("bad request", new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

}
