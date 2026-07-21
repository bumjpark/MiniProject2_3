package com.example.demo.calendar.dto;

import com.example.demo.calendar.entity.Label;
import com.example.demo.calendar.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// 일정 조회를 위한 response
@Getter
@AllArgsConstructor
public class ScheduleResponse {

    private Long scheduleId;
    private Long calendarId;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Label label;

    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getScheduleId(),
                schedule.getCalendarId(),
                schedule.getTitle(),
                schedule.getStartDateTime(),
                schedule.getEndDateTime(),
                schedule.getLabel()
        );
    }
}
