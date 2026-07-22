package com.example.demo.todo.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Todo 생성 요청")
public record TodoCreateRequest(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,
        @Schema(description = "TodoList ID. 목록 없이 생성할 경우 생략", example = "1")
        Long listId,
        @Schema(description = "카테고리 ID. 카테고리 없이 생성할 경우 생략", example = "1")
        Long categoryId,
        @Schema(description = "Todo 내용", example = "프로젝트 발표 자료 준비")
        String content,
        @Schema(description = "마감일. 마감일이 없으면 생략", example = "2026-07-30")
        LocalDate deadline
) {
}
