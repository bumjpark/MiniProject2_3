package com.example.demo.todo.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Todo 수정 요청")
public record TodoUpdateRequest(
        @Schema(description = "이동할 TodoList ID. 생략하면 기존 목록 유지", example = "2")
        Long listId,
        @Schema(description = "변경할 카테고리 ID. 생략하면 기존 카테고리 유지", example = "2")
        Long categoryId,
        @Schema(description = "수정할 Todo 내용", example = "수정된 발표 자료 준비")
        String content,
        @Schema(description = "수정할 마감일", example = "2026-08-01")
        LocalDate deadline
) {
}
