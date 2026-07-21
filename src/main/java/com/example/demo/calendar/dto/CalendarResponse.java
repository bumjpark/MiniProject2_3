package com.example.demo.calendar.dto;

import java.util.List;

import com.example.demo.auth.entity.User;
import com.example.demo.calendar.entity.Calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 캘린더 조회를 위한 response
@Getter
@AllArgsConstructor
public class CalendarResponse {

    private Long calendarId;
    private String name;
    private List<String> members;

    public static CalendarResponse from(Calendar calendar) {
        List<String> memberNames = calendar.getMembers().stream()
                .map(User::getName)
                .toList();
        return new CalendarResponse(calendar.getCalendarId(), calendar.getName(), memberNames);
    }
}
