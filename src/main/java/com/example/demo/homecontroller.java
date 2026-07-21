package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class homecontroller {
	@GetMapping("/")
	public String home() {
		return "로그인 성공";
	}
	
	@GetMapping("/test")
	public String test() {
	    return "JWT Success";
	}
}
