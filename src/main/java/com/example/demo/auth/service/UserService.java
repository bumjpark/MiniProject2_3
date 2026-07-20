package com.example.demo.auth.service;

import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.auth.dto.UserResponseDto;

public interface UserService {
	UserResponseDto signup(SignupRequestDto request);
}
