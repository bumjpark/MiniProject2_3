package com.example.demo.todo.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Todo 수정 요청")
public record TodoUpdateRequest(
        @Schema(description = "이동할 TodoList ID. 생략하면 기존 목록 유지", example = "2")
        @Positive(message = "TodoList ID는 양수여야 합니다.")
        Long listId,
        @Schema(description = "변경할 카테고리 ID. 생략하면 기존 카테고리 유지", example = "2")
        @Positive(message = "카테고리 ID는 양수여야 합니다.")
        Long categoryId,
        @Schema(description = "수정할 Todo 내용", example = "수정된 발표 자료 준비")
        @NotBlank(message = "Todo 내용은 필수입니다.")
        @Size(max = 255, message = "Todo 내용은 255자 이하여야 합니다.")
        String content,
        @Schema(description = "수정할 마감일", example = "2026-08-01")
        @FutureOrPresent(message = "마감일은 오늘 이후여야 합니다.")
        LocalDate deadline
) {
}
