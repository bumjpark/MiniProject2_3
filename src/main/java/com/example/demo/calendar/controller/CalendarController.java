package com.example.demo.calendar.controller;

import java.util.List;

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

import com.example.demo.calendar.dto.CalendarRequest;
import com.example.demo.calendar.dto.CalendarResponse;
import com.example.demo.calendar.entity.Calendar;
import com.example.demo.calendar.service.CalendarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Calendar", description = "공유 캘린더 CRUD API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/calendars")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    // 캘린더 생성 (생성자는 자동으로 멤버가 됨)
    @Operation(summary = "캘린더 생성", description = "이름과 초대 멤버 목록으로 공유 캘린더를 생성한다. 생성자는 자동으로 멤버로 등록된다.")
    @PostMapping
    public ResponseEntity<CalendarResponse> createCalendar(@RequestBody CalendarRequest request) {
        Calendar calendar = calendarService.create(request.getName(), request.getMemberIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(CalendarResponse.from(calendar));
    }

    // 캘린더 전체 조회 (내가 속한 캘린더만)
    @Operation(summary = "내 캘린더 전체 조회", description = "내가 멤버로 속한 캘린더 목록을 조회한다.")
    @GetMapping
    public ResponseEntity<List<CalendarResponse>> getCalendars() {
        List<CalendarResponse> responses = calendarService.getAll().stream()
                .map(CalendarResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // 캘린더 1개 조회
    @Operation(summary = "캘린더 단건 조회", description = "calendarId로 캘린더 하나를 조회한다.")
    @GetMapping("/{calendarId}")
    public ResponseEntity<CalendarResponse> getCalendar(@PathVariable("calendarId") Long calendarId) {
        return ResponseEntity.ok(CalendarResponse.from(calendarService.getById(calendarId)));
    }

    // 캘린더 이름/멤버 수정
    @Operation(summary = "캘린더 수정", description = "캘린더 이름 또는 초대 멤버 목록을 수정한다.")
    @PutMapping("/{calendarId}")
    public ResponseEntity<CalendarResponse> updateCalendar(@PathVariable("calendarId") Long calendarId,
            @RequestBody CalendarRequest request) {
        Calendar calendar = calendarService.update(calendarId, request.getName(), request.getMemberIds());
        return ResponseEntity.ok(CalendarResponse.from(calendar));
    }

    // 캘린더 1개 삭제
    @Operation(summary = "캘린더 삭제", description = "calendarId로 캘린더를 삭제한다.")
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable("calendarId") Long calendarId) {
        calendarService.delete(calendarId);
        return ResponseEntity.noContent().build();
    }

    // 캘린더 멤버 추가
    @Operation(summary = "캘린더 멤버 추가", description = "캘린더에 유저를 멤버로 초대한다.")
    @PostMapping("/{calendarId}/members/{userId}")
    public ResponseEntity<CalendarResponse> addMember(@PathVariable("calendarId") Long calendarId,
            @PathVariable("userId") Long userId) {
        Calendar calendar = calendarService.addMember(calendarId, userId);
        return ResponseEntity.ok(CalendarResponse.from(calendar));
    }

    // 캘린더 멤버 제거
    @Operation(summary = "캘린더 멤버 제거", description = "캘린더 멤버 목록에서 유저를 제거한다.")
    @DeleteMapping("/{calendarId}/members/{userId}")
    public ResponseEntity<CalendarResponse> removeMember(@PathVariable("calendarId") Long calendarId,
            @PathVariable("userId") Long userId) {
        Calendar calendar = calendarService.removeMember(calendarId, userId);
        return ResponseEntity.ok(CalendarResponse.from(calendar));
    }
}
