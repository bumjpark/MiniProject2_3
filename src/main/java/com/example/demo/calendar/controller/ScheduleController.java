package com.example.demo.calendar.controller;

import com.example.demo.calendar.dto.ScheduleRequest;
import com.example.demo.calendar.dto.ScheduleResponse;
import com.example.demo.calendar.entity.Schedule;
import com.example.demo.calendar.service.ScheduleService;
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

@RestController
@RequestMapping("/api/calendars/{calendarId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 추가
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@PathVariable Long calendarId,
                                                             @RequestBody ScheduleRequest request) {
        Schedule schedule = scheduleService.create(calendarId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ScheduleResponse.from(schedule));
    }

    // 캘린더별 일정 전체 조회
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getSchedules(@PathVariable Long calendarId) {
        List<ScheduleResponse> responses = scheduleService.getAllByCalendar(calendarId).stream()
                .map(ScheduleResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // 일정 1개 조회 
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable Long calendarId,
                                                          @PathVariable Long id) {
        return ResponseEntity.ok(ScheduleResponse.from(scheduleService.getById(calendarId, id)));
    }

    // 일정 수정
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponse> updateSchedule(@PathVariable Long calendarId,
                                                             @PathVariable Long id,
                                                             @RequestBody ScheduleRequest request) {
        Schedule schedule = scheduleService.update(calendarId, id, request);
        return ResponseEntity.ok(ScheduleResponse.from(schedule));
    }

    // 일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long calendarId, @PathVariable Long id) {
        scheduleService.delete(calendarId, id);
        return ResponseEntity.noContent().build();
    }
}
