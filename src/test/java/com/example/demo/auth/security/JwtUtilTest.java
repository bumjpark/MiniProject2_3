package com.example.demo.auth.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("액세스토큰_생성후_이메일추출")
    void accessTokenCreateAndExtractEmail() {
        String email = "test@test.com";
        String token = jwtUtil.createAccessToken(email);
        String extractedEmail = jwtUtil.getEmailFromToken(token);

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("액세스토큰_검증")
    void accessTokenValidate() {
        String token = jwtUtil.createAccessToken("test@test.com");
        boolean result = jwtUtil.validateToken(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("리프레시토큰_생성")
    void refreshTokenCreate() {
        String token = jwtUtil.createRefreshToken("test@test.com");

        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("잘못된_토큰은_검증실패")
    void invalidTokenValidate() {
        assertThat(jwtUtil.validateToken("invalid-token")).isFalse();
    }
}