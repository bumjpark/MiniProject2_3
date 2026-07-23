package com.example.demo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "토큰 재발급 응답 DTO")
public class TokenRefreshResponseDto {

    @Schema(description = "새로 발급된 Access 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
}
