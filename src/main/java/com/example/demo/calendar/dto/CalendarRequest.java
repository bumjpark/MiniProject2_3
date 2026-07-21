package com.example.demo.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 캘린더 생성/수정을 위한 request
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarRequest {

    @Schema(description = "캘린더 이름", example = "팀 캘린더")
    private String name;

    @Schema(description = "초대할 멤버의 사용자 id 목록")
    private List<Long> memberIds;
}
