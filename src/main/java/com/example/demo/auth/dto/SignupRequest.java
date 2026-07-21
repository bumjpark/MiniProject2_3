package com.example.demo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

// 회원가입 요청 
public class SignupRequest {

    private String email;
    private String password;
}
