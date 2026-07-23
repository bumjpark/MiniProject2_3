package com.example.demo.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "토큰 재발급 요청 DTO")
public class TokenRefreshRequestDto {

    @Schema(description = "Refresh 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
}
