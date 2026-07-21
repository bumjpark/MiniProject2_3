package com.example.demo.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDto {
	
	private String email;
	
	private String password;
	
	private String name;
}
