package com.example.demo.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.LoginResponseDto;
import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.auth.dto.TokenRefreshRequestDto;
import com.example.demo.auth.dto.TokenRefreshResponseDto;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.auth.security.CustomUserDetails;
import com.example.demo.auth.security.JwtUtil;
import com.example.demo.auth.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Tag(name = "인증 API", description = "로그인 및 로그아웃 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final JwtUtil jwtUtil;
        private final UserRepository userRepository;
        private final UserService userService;

        @Operation(summary = "로그인", description = "이메일과 비밀번호로 인증 후 AccessToken 및 RefreshToken을 발급받습니다.")
        @PostMapping("/login")
        public LoginResponseDto login(@RequestBody LoginRequestDto request) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                String token = jwtUtil.createAccessToken(
                                userDetails.getUsername());
                String refreshToken = jwtUtil.createRefreshToken(
                                userDetails.getUsername());

                Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
                if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        user.setRefreshToken(refreshToken);
                        userRepository.save(user);
                }

                return new LoginResponseDto(token, refreshToken);
        }

        @Operation(summary = "토큰 재발급", description = "Refresh Token을 이용해 새로운 Access Token을 발급받습니다.")
        @PostMapping("/refresh")
        public ResponseEntity<TokenRefreshResponseDto> refresh(@RequestBody TokenRefreshRequestDto request) {
                TokenRefreshResponseDto response = userService.refreshAccessToken(request.getRefreshToken());
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "로그아웃", description = "Bearer 토큰 인증을 통해 로그아웃을 수행하고 RefreshToken을 무효화합니다.", security = @SecurityRequirement(name = "bearerAuth"))
        @PostMapping("/logout")
        public ResponseEntity<String> logout() {
                // JwtFilter에서 이미 인증된 상태이므로 SecurityContext에서 이메일 추출
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();

                userService.logout(email);

                return ResponseEntity.ok("로그아웃 되었습니다.");
        }
}