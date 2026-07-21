package com.example.demo.calendar.dto;

import com.example.demo.calendar.entity.Label;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 일정 생성/수정을 위한 request
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {

    @Schema(description = "일정 제목 (필수)", example = "주간 회의")
    private String title;

    @Schema(description = "시작 시각. 생략하면 현재 시간을 1시간 단위로 잘라서 사용", example = "2026-07-21T16:00:00")
    private LocalDateTime startDateTime;

    @Schema(description = "종료 시각. 생략하면 시작 시각 + 1시간으로 설정", example = "2026-07-21T17:00:00")
    private LocalDateTime endDateTime;

    @Schema(description = "색깔 라벨")
    private Label label;
}
