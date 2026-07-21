package com.example.demo.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.CustomUserDetails;
import com.example.demo.auth.dto.ErrorResponse;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextLogoutHandler logoutHandler =
            new SecurityContextLogoutHandler();
    private final CookieClearingLogoutHandler cookieClearingLogoutHandler =
            new CookieClearingLogoutHandler("JSESSIONID");

    public AuthController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (loginRequest.email() == null
                || loginRequest.email().isBlank()
                || loginRequest.password() == null
                || loginRequest.password().isBlank()) {
            return unauthorized();
        }

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.email(),
                                    loginRequest.password()
                            )
                    );

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(
                    context,
                    request,
                    response
            );

            CustomUserDetails loginUser =
                    (CustomUserDetails) authentication.getPrincipal();

            return ResponseEntity.ok(new LoginResponse(
                    loginUser.getUserId(),
                    loginUser.getRole()
            ));
        } catch (AuthenticationException exception) {
            SecurityContextHolder.clearContext();
            return unauthorized();
        }
    }

    @GetMapping("/me")
    public LoginResponse me(
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        return new LoginResponse(
                loginUser.getUserId(),
                loginUser.getRole()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        cookieClearingLogoutHandler.logout(
                request,
                response,
                authentication
        );
        logoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<ErrorResponse> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "이메일 또는 비밀번호가 올바르지 않습니다."
                ));
    }
}
