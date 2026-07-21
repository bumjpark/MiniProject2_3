package com.example.demo.auth.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.CustomUserDetails;

@RestController
public class LoginPageController {

    private static final MediaType TEXT_HTML_UTF8 =
            new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);

    @GetMapping(
            value = "/login",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> loginPage(
            @RequestParam(required = false) String error
    ) {
        String errorMessage = error == null
                ? ""
                : "<p style=\"color:#c62828\">"
                        + "이메일 또는 비밀번호가 올바르지 않습니다."
                        + "</p>";

        String html = """
                <!doctype html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>로그인</title>
                </head>
                <body>
                  <main style="max-width:360px;margin:80px auto;font-family:sans-serif">
                    <h1>로그인</h1>
                    %s
                    <form method="post" action="/login">
                      <label>이메일<br>
                        <input type="email" name="email" required
                               style="width:100%%;margin:6px 0 16px;padding:10px">
                      </label>
                      <label>비밀번호<br>
                        <input type="password" name="password" required
                               style="width:100%%;margin:6px 0 16px;padding:10px">
                      </label>
                      <button type="submit" style="padding:10px 20px">로그인</button>
                    </form>
                  </main>
                </body>
                </html>
                """.formatted(errorMessage);

        return ResponseEntity.ok()
                .contentType(TEXT_HTML_UTF8)
                .body(html);
    }

    @GetMapping(
            value = "/login-success",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> loginSuccess(
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        String html = """
                <!doctype html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>로그인 성공</title>
                </head>
                <body>
                  <main style="max-width:480px;margin:80px auto;font-family:sans-serif">
                    <h1>로그인 성공</h1>
                    <p>사용자 ID: %d</p>
                    <p>권한: %s</p>
                    <p><a href="/api/admin/test">관리자 API 확인</a></p>
                    <p><a href="/api/todo-lists">Todo 목록 확인</a></p>
                    <form method="post" action="/api/auth/logout">
                      <button type="submit">로그아웃</button>
                    </form>
                  </main>
                </body>
                </html>
                """.formatted(
                        loginUser.getUserId(),
                        loginUser.getRole()
                );

        return ResponseEntity.ok()
                .contentType(TEXT_HTML_UTF8)
                .body(html);
    }
}
