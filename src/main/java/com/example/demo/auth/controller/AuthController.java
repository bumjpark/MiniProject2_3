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
import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.auth.security.CustomUserDetails;
import com.example.demo.auth.security.JwtUtil;
import com.example.demo.auth.service.UserService;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final JwtUtil jwtUtil;
        private final UserRepository userRepository;
        private final UserService userService;

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

        @PostMapping("/logout")
        public ResponseEntity<String> logout() {
                // JwtFilter에서 이미 인증된 상태이므로 SecurityContext에서 이메일 추출
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();

                userService.logout(email);

                return ResponseEntity.ok("로그아웃 되었습니다.");
        }
}