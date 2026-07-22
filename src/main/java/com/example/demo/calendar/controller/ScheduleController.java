package com.example.demo.calendar.controller;

import com.example.demo.calendar.dto.ScheduleRequest;
import com.example.demo.calendar.dto.ScheduleResponse;
import com.example.demo.calendar.entity.Schedule;
import com.example.demo.calendar.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Schedule", description = "캘린더 일정 CRUD API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/calendars/{calendarId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 추가
    @Operation(summary = "일정 생성", description = "제목은 필수이며, 시작/종료 시간을 생략하면 현재 시간을 기준으로 1시간 단위 디폴트가 적용된다.")
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@PathVariable("calendarId") Long calendarId,
                                                             @Valid @RequestBody ScheduleRequest request) {
        Schedule schedule = scheduleService.create(calendarId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ScheduleResponse.from(schedule));
    }

    // 캘린더별 일정 전체 조회
    @Operation(summary = "캘린더별 일정 전체 조회", description = "특정 캘린더에 속한 모든 일정을 조회한다.")
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getSchedules(@PathVariable("calendarId") Long calendarId) {
        List<ScheduleResponse> responses = scheduleService.getAllByCalendar(calendarId).stream()
                .map(ScheduleResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // 일정 1개 조회
    @Operation(summary = "일정 단건 조회", description = "scheduleId로 일정 하나를 조회한다.")
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable("calendarId") Long calendarId,
                                                          @PathVariable("scheduleId") Long scheduleId) {
        return ResponseEntity.ok(ScheduleResponse.from(scheduleService.getById(calendarId, scheduleId)));
    }

    // 일정 수정
    @Operation(summary = "일정 수정", description = "값이 들어온 필드만 갱신된다.")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(@PathVariable("calendarId") Long calendarId,
                                                             @PathVariable("scheduleId") Long scheduleId,
                                                             @Valid @RequestBody ScheduleRequest request) {
        Schedule schedule = scheduleService.update(calendarId, scheduleId, request);
        return ResponseEntity.ok(ScheduleResponse.from(schedule));
    }

    // 일정 삭제
    @Operation(summary = "일정 삭제", description = "scheduleId로 일정을 삭제한다.")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable("calendarId") Long calendarId,
                                                @PathVariable("scheduleId") Long scheduleId) {
        scheduleService.delete(calendarId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}
