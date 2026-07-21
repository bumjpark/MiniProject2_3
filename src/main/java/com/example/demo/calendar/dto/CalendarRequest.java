package com.example.demo.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 캘린더 생성/수정을 위한 request
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarRequest {

    private String name;
    private List<String> members;
}
