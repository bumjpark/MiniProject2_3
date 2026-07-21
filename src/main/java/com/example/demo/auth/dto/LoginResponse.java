package com.example.demo.auth.dto;

public record LoginResponse(
        Long userId,
        String role
) {
}
