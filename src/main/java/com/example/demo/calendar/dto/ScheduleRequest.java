package com.example.demo.calendar.dto;

import com.example.demo.calendar.entity.Label;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// 일정 생성/수정을 위한 request
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {

    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<String> participants;
    private Label label;
}
