package com.example.demo.calendar.controller;

import com.example.demo.calendar.dto.CalendarRequest;
import com.example.demo.calendar.dto.CalendarResponse;
import com.example.demo.calendar.entity.Calendar;
import com.example.demo.calendar.service.CalendarService;
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
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    // 캘린더 생성 
    @PostMapping
    public ResponseEntity<CalendarResponse> createCalendar(@RequestBody CalendarRequest request) {
        Calendar calendar = calendarService.create(request.getName(), request.getMembers());
        return ResponseEntity.status(HttpStatus.CREATED).body(CalendarResponse.from(calendar));
    }

    // 캘린더 전체 조회 
    @GetMapping
    public ResponseEntity<List<CalendarResponse>> getCalendars() {
        List<CalendarResponse> responses = calendarService.getAll().stream()
                .map(CalendarResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // 캘린더 1개 조회
    @GetMapping("/{id}")
    public ResponseEntity<CalendarResponse> getCalendar(@PathVariable Long id) {
        return ResponseEntity.ok(CalendarResponse.from(calendarService.getById(id)));
    }

    // 캘린더 이름/멤버 수정
    @PutMapping("/{id}")
    public ResponseEntity<CalendarResponse> updateCalendar(@PathVariable Long id,
                                                             @RequestBody CalendarRequest request) {
        Calendar calendar = calendarService.update(id, request.getName(), request.getMembers());
        return ResponseEntity.ok(CalendarResponse.from(calendar));
    }

    // 캘린더 1개 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable Long id) {
        calendarService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
