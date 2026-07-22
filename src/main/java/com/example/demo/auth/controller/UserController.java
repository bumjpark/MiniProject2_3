package com.example.demo.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.auth.dto.UserResponseDto;
import com.example.demo.auth.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "사용자 API", description = "회원가입 등 사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	
	private final UserService userService;
	
	@Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
	@PostMapping("/signup")
	public UserResponseDto signup(@RequestBody SignupRequestDto request) {
		return userService.signup(request);
	}

}
