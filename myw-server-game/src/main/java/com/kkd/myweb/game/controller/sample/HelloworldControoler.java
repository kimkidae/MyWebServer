package com.kkd.myweb.game.controller.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
public class HelloworldControoler {

	@AllArgsConstructor
	@Data
	class Hello{
		private String message;
	}

	@GetMapping("/")
	public Hello hellowrold() {
		return new Hello("hello world");
	}

}
