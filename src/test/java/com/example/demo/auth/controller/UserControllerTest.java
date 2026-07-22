package com.example.demo.auth.controller;

import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.auth.dto.UserResponseDto;
import com.example.demo.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("회원가입 성공 시 200 OK와 생성된 사용자 정보 반환")
    void signup_Success() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        SignupRequestDto request = new SignupRequestDto("test@test.com", "1234", "testUser");
        UserResponseDto response = new UserResponseDto(1L, "test@test.com", "testUser");

        given(userService.signup(any(SignupRequestDto.class))).willReturn(response);

        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.name").value("testUser"));
    }
}
