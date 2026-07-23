package com.example.demo.auth.service;

import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.auth.dto.TokenRefreshResponseDto;
import com.example.demo.auth.dto.UserResponseDto;

public interface UserService {
	UserResponseDto signup(SignupRequestDto request);
	void logout(String email);
	TokenRefreshResponseDto refreshAccessToken(String refreshToken);
}
