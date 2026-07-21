package com.example.demo.auth.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.LoginResponseDto;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.auth.security.CustomUserDetails;
import com.example.demo.auth.security.JwtUtil;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.createAccessToken(
                userDetails.getUsername()
        );
        String refreshToken = jwtUtil.createRefreshToken(
                userDetails.getUsername()
        );

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }

        return new LoginResponseDto(token, refreshToken);
    }
}