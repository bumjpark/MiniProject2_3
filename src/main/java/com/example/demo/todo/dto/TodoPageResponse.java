package com.example.demo.todo.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Todo 페이지 조회 응답")
public record TodoPageResponse(
        @Schema(description = "현재 페이지의 Todo 목록")
        List<TodoResponse> content,
        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        int page,
        @Schema(description = "페이지 크기", example = "20")
        int size,
        @Schema(description = "전체 Todo 개수", example = "42")
        long totalElements,
        @Schema(description = "전체 페이지 수", example = "3")
        int totalPages,
        @Schema(description = "첫 페이지 여부", example = "true")
        boolean first,
        @Schema(description = "마지막 페이지 여부", example = "false")
        boolean last
) {

    public static TodoPageResponse from(Page<TodoResponse> todoPage) {
        return new TodoPageResponse(
                todoPage.getContent(),
                todoPage.getNumber(),
                todoPage.getSize(),
                todoPage.getTotalElements(),
                todoPage.getTotalPages(),
                todoPage.isFirst(),
                todoPage.isLast()
        );
    }
}
