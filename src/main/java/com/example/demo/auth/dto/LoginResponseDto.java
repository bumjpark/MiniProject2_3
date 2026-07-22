package com.example.demo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {
	 @Schema(description = "Access 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
	 private String token;

	 @Schema(description = "Refresh 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
	 private String refreshToken;
}
