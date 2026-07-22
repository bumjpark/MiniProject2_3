package com.example.demo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignupRequestDto {
	
	@Schema(description = "사용자 이메일", example = "user@example.com")
	private String email;
	
	@Schema(description = "비밀번호", example = "password123!")
	private String password;
	
	@Schema(description = "사용자 이름", example = "홍길동")
	private String name;
}
