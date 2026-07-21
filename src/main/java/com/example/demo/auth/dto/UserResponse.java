package com.example.demo.auth.dto;

import com.example.demo.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 회원가입 성공 시의 response
public class UserResponse {

    private Long id;
    private String email;
    private String role;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole());
    }
}
