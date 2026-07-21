package com.example.demo.calendar.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 일정 Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    private Long id;
    private Long calendarId;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @Builder.Default
    private List<String> participants = new ArrayList<>();

    private Label label;
}
