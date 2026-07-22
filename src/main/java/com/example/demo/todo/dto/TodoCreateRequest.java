package com.example.demo.todo.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Todo 생성 요청")
public record TodoCreateRequest(
        @Schema(description = "TodoList ID. 목록 없이 생성할 경우 생략", example = "1")
        @Positive(message = "TodoList ID는 양수여야 합니다.")
        Long listId,
        @Schema(description = "카테고리 ID. 카테고리 없이 생성할 경우 생략", example = "1")
        @Positive(message = "카테고리 ID는 양수여야 합니다.")
        Long categoryId,
        @Schema(description = "Todo 내용", example = "프로젝트 발표 자료 준비")
        @NotBlank(message = "Todo 내용은 필수입니다.")
        @Size(max = 255, message = "Todo 내용은 255자 이하여야 합니다.")
        String content,
        @Schema(description = "마감일. 마감일이 없으면 생략", example = "2026-07-30")
        @FutureOrPresent(message = "마감일은 오늘 이후여야 합니다.")
        LocalDate deadline
) {
}
