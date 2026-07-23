package com.example.demo.calendar;

import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.calendar.dto.CalendarRequest;
import com.example.demo.calendar.dto.ScheduleRequest;
import com.example.demo.calendar.entity.Label;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * 회원1 가입 -> 캘린더 생성 -> 캘린더 제목 수정 -> 스케줄 생성 -> 스케줄 수정
 * -> 회원2 가입 -> 회원1이 회원2를 캘린더에 초대 -> 회원2가 스케줄 삭제
 */
@SpringBootTest
class CalendarFlowTest {

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Test
    void testAll() throws Exception {
        // 실제 SecurityFilterChain(JwtFilter 포함)을 그대로 통과하는 MockMvc 구성
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        long suffix = System.currentTimeMillis();
        String email1 = "member1_" + suffix + "@example.com";
        String email2 = "member2_" + suffix + "@example.com";

        // 회원1 추가 + 로그인
        signup("회원1", email1, "pw1234!");
        String token1 = login(email1, "pw1234!");

        // 캘린더 생성 (회원1이 생성 -> 자동으로 멤버가 됨)
        Long calendarId = createCalendar(token1, "테스트 캘린더");

        // 캘린더 제목 수정
        JsonNode updatedCalendar = updateCalendar(token1, calendarId, "수정된 캘린더 이름");
        assertThat(updatedCalendar.get("name").asText()).isEqualTo("수정된 캘린더 이름");

        // 스케줄 추가
        Long scheduleId = createSchedule(token1, calendarId, "테스트 일정");

        // 스케줄 수정
        JsonNode updatedSchedule = updateSchedule(token1, calendarId, scheduleId, "수정된 일정 제목");
        assertThat(updatedSchedule.get("title").asText()).isEqualTo("수정된 일정 제목");

        // 회원2 추가 + 로그인
        Long userId2 = signup("회원2", email2, "pw1234!");
        String token2 = login(email2, "pw1234!");

        // 회원1이 회원2를 캘린더에 초대
        JsonNode calendarAfterInvite = addMember(token1, calendarId, userId2);
        assertThat(calendarAfterInvite.get("members").toString()).contains("회원2");

        // 회원2가 스케줄 삭제 (멤버라면 본인이 만들지 않은 일정도 삭제 가능해야 함)
        System.out.println("[일정 삭제] 회원2가 scheduleId=" + scheduleId + " 삭제 시도");
        mockMvc.perform(delete("/calendars/{calendarId}/schedules/{scheduleId}", calendarId, scheduleId)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isNoContent());
        System.out.println("[일정 삭제] scheduleId=" + scheduleId + " 삭제 완료 (204 No Content)");

        // 삭제됐는지 재확인 (초대받은 회원2가 조회해도 404)
        System.out.println("[재확인] 삭제된 scheduleId=" + scheduleId + " 재조회");
        mockMvc.perform(get("/calendars/{calendarId}/schedules/{scheduleId}", calendarId, scheduleId)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isNotFound());
        System.out.println("[재확인] 404 Not Found 확인됨 -> 시나리오 종료");
    }

    // POST /users/signup 호출해서 회원가입시키고, 생성된 사용자 id를 반환
    private Long signup(String name, String email, String password) throws Exception {
        SignupRequestDto request = new SignupRequestDto();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);

        MvcResult result = mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        Long userId = readJson(result).get("id").asLong();
        System.out.println("[회원가입] " + name + "(" + email + ") -> userId=" + userId);
        return userId;
    }

    // POST /users/login 호출해서 로그인시키고, 발급된 액세스 토큰을 반환
    private String login(String email, String password) throws Exception {
        LoginRequestDto request = new LoginRequestDto(email, password);

        MvcResult result = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String token = readJson(result).get("token").asText();
        System.out.println("[로그인] " + email + " -> 토큰 발급됨 (" + token.substring(0, 10) + "...)");
        return token;
    }

    // POST /calendars 호출해서 캘린더를 생성하고, 생성된 calendarId를 반환
    private Long createCalendar(String token, String name) throws Exception {
        CalendarRequest request = new CalendarRequest(name, null);

        MvcResult result = mockMvc.perform(post("/calendars")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Long calendarId = readJson(result).get("calendarId").asLong();
        System.out.println("[캘린더 생성] calendarId=" + calendarId + ", name=" + name);
        return calendarId;
    }

    // PUT /calendars/{calendarId} 호출해서 캘린더 이름을 수정하고, 응답 본문을 반환
    private JsonNode updateCalendar(String token, Long calendarId, String newName) throws Exception {
        CalendarRequest request = new CalendarRequest(newName, null);

        MvcResult result = mockMvc.perform(put("/calendars/{calendarId}", calendarId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = readJson(result);
        System.out.println("[캘린더 수정] calendarId=" + calendarId + " -> name=" + response.get("name").asText());
        return response;
    }

    // POST /calendars/{calendarId}/schedules 호출해서 일정을 생성하고, 생성된 scheduleId를 반환
    private Long createSchedule(String token, Long calendarId, String title) throws Exception {
        ScheduleRequest request = new ScheduleRequest(title, null, null, Label.BLUE);

        MvcResult result = mockMvc.perform(post("/calendars/{calendarId}/schedules", calendarId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Long scheduleId = readJson(result).get("scheduleId").asLong();
        System.out.println("[일정 생성] scheduleId=" + scheduleId + ", title=" + title);
        return scheduleId;
    }

    // PUT /calendars/{calendarId}/schedules/{scheduleId} 호출해서 일정 제목을 수정하고, 응답 본문을 반환
    private JsonNode updateSchedule(String token, Long calendarId, Long scheduleId, String newTitle) throws Exception {
        ScheduleRequest request = new ScheduleRequest(newTitle, null, null, Label.GREEN);

        MvcResult result = mockMvc.perform(put("/calendars/{calendarId}/schedules/{scheduleId}", calendarId, scheduleId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = readJson(result);
        System.out.println("[일정 수정] scheduleId=" + scheduleId + " -> title=" + response.get("title").asText());
        return response;
    }

    // POST /calendars/{calendarId}/members/{userId} 호출해서 멤버를 초대하고, 응답 본문(갱신된 멤버 목록)을 반환
    private JsonNode addMember(String token, Long calendarId, Long userId) throws Exception {
        MvcResult result = mockMvc.perform(post("/calendars/{calendarId}/members/{userId}", calendarId, userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = readJson(result);
        System.out.println("[멤버 초대] calendarId=" + calendarId + "에 userId=" + userId
                + " 추가 -> members=" + response.get("members"));
        return response;
    }

    // MvcResult의 응답 본문(JSON 문자열)을 JsonNode로 파싱
    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
