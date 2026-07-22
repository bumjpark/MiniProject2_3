package com.example.demo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "사용자 정보 응답 DTO")
public class UserResponseDto {
	@Schema(description = "사용자 식별자 ID", example = "1")
	private Long id;

	@Schema(description = "사용자 이메일", example = "user@example.com")
	private String email;

	@Schema(description = "사용자 이름", example = "홍길동")
	private String name;
}
