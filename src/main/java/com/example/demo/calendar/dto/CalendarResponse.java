package com.example.demo.calendar.dto;

import com.example.demo.calendar.entity.Calendar;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// 캘린더 조회를 위한 response
@Getter
@AllArgsConstructor
public class CalendarResponse {

    private Long id;
    private String name;
    private List<String> members;

    public static CalendarResponse from(Calendar calendar) {
        return new CalendarResponse(calendar.getId(), calendar.getName(), calendar.getMembers());
    }
}
