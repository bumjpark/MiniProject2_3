package com.example.demo.auth.service;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("이메일로 사용자 조회 성공 시 UserDetails 반환")
    void loadUserByUsername_Success() {

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .name("테스터")
                .build();

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("존재하지 않는 이메일 조회 시 UsernameNotFoundException 발생")
    void loadUserByUsername_UserNotFound() {
        String email = "notfound@example.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }
}
