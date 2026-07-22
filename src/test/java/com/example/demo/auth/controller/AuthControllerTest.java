package com.example.demo.auth.controller;

import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.LoginResponseDto;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.auth.security.CustomUserDetails;
import com.example.demo.auth.security.JwtUtil;
import com.example.demo.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("로그인 성공 시 AccessToken 및 RefreshToken 반환")
    void login_Success() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        LoginRequestDto request = new LoginRequestDto("test@test.com", "1234");

        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("testUser")
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(jwtUtil.createAccessToken("test@test.com")).willReturn("mock-access-token");
        given(jwtUtil.createRefreshToken("test@test.com")).willReturn("mock-refresh-token");
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(user));

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"));
    }

    @Test
    @DisplayName("로그아웃 성공 시 200 OK와 로그아웃 완료 메시지 반환")
    void logout_Success() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .addFilter(new org.springframework.web.filter.CharacterEncodingFilter("UTF-8", true))
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken("test@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(post("/users/logout"))
                .andExpect(status().isOk());

        verify(userService).logout("test@test.com");
    }
}
