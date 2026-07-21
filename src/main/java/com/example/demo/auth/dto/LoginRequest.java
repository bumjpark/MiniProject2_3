package com.example.demo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

// 로그인 요청
public class LoginRequest {

    private String email;
    private String password;
}
