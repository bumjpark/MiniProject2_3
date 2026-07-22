package com.example.demo.auth.service;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.auth.dto.UserResponseDto;
import com.example.demo.auth.repository.UserRepository;

import com.example.demo.auth.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.auth.dto.SignupRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("회원가입_성공")
    void testSignUp_Success() {
        SignupRequestDto request = new SignupRequestDto(
                "test@test.com",
                "1234",
                "test");

        User user = User.builder()
                .id(1L)
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .build();

        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.empty());

        given(passwordEncoder.encode("1234")).willReturn("encoded_1234");

        given(userRepository.save(any(User.class))).willReturn(user);

        UserResponseDto response = userService.signup(request);

        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getName()).isEqualTo("test");

    }

    @Test
    @DisplayName("로그아웃_성공")
    void testLogout_Success() {
        // given
        String email = "test@test.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .refreshToken("some_refresh_token")
                .build();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        // when
        userService.logout(email);
        // then
        assertThat(user.getRefreshToken()).isNull(); // Refresh Token이 null로 변경되었는지 검증
        verify(userRepository).save(user); // userRepository.save()가 호출되었는지 검증
    }

}
