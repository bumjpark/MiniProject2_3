package com.example.demo.calendar;

import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.SignupRequestDto;
import com.example.demo.calendar.dto.CalendarRequest;
import com.example.demo.calendar.dto.ScheduleRequest;
import com.example.demo.calendar.entity.Label;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
 * 캘린더/일정 관련 시나리오를 각각 독립된 @Test로 검증한다.
 * @BeforeEach에서 매번 회원1 가입+로그인+캘린더 생성까지 새로 해둠
 */
@SpringBootTest
class CalendarFlowTest {

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private String token1;
    private JsonNode createdCalendar;
    private Long calendarId;

    @BeforeEach
    void setUp() throws Exception {
        // 실제 SecurityFilterChain(JwtFilter 포함)을 그대로 통과하는 MockMvc 구성
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        String email1 = "member1_" + System.nanoTime() + "@example.com";

        // 회원1 가입 + 로그인
        signup("회원1", email1, "pw1234!");
        token1 = login(email1, "pw1234!");

        // 캘린더 생성 (회원1이 생성 -> 자동으로 멤버가 됨). 각 테스트가 여기서부터 시작
        createdCalendar = createCalendar(token1, "테스트 캘린더");
        calendarId = createdCalendar.get("calendarId").asLong();
    }

    @Test
    @DisplayName("캘린더 생성 후 생성자 -> 멤버")
    void 캘린더_생성_시_생성자가_자동으로_멤버가_된다() {
        assertThat(createdCalendar.get("members").toString()).contains("회원1");
    }

    @Test
    @DisplayName("캘린더 이름 수정")
    void 캘린더_이름을_수정할_수_있다() throws Exception {
        JsonNode updatedCalendar = updateCalendar(token1, calendarId, "수정된 캘린더 이름");

        assertThat(updatedCalendar.get("name").asText()).isEqualTo("수정된 캘린더 이름");
    }

    @Test
    @DisplayName("일정 생성/수정")
    void 일정을_생성하고_수정할_수_있다() throws Exception {
        Long scheduleId = createSchedule(token1, calendarId, "테스트 일정");

        JsonNode updatedSchedule = updateSchedule(token1, calendarId, scheduleId, "수정된 일정 제목");

        assertThat(updatedSchedule.get("title").asText()).isEqualTo("수정된 일정 제목");
    }

    @Test
    @DisplayName("멤버 초대")
    void 멤버를_초대하면_캘린더_멤버_목록에_추가된다() throws Exception {
        String email2 = "member2_" + System.nanoTime() + "@example.com";
        Long userId2 = signup("회원2", email2, "pw1234!");

        JsonNode calendarAfterInvite = addMember(token1, calendarId, userId2);

        assertThat(calendarAfterInvite.get("members").toString()).contains("회원2");
    }

    @Test
    @DisplayName("초대된 멤버 : 일정 삭제")
    void 초대된_멤버는_다른_사람이_만든_일정도_삭제할_수_있다() throws Exception {
        Long scheduleId = createSchedule(token1, calendarId, "테스트 일정");

        String email2 = "member2_" + System.nanoTime() + "@example.com";
        Long userId2 = signup("회원2", email2, "pw1234!");
        String token2 = login(email2, "pw1234!");
        addMember(token1, calendarId, userId2);

        // 회원2가 회원1이 만든 일정 삭제 (멤버라면 본인이 만들지 않은 일정도 삭제 가능해야 함)
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

    // POST /calendars 호출해서 캘린더를 생성하고, 응답 본문을 반환
    private JsonNode createCalendar(String token, String name) throws Exception {
        CalendarRequest request = new CalendarRequest(name, null);

        MvcResult result = mockMvc.perform(post("/calendars")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode response = readJson(result);
        System.out.println("[캘린더 생성] calendarId=" + response.get("calendarId").asLong() + ", name=" + name);
        return response;
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
