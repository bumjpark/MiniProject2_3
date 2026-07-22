package com.example.demo.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 생성 요청")
public record CategoryCreateRequest(
        @Schema(description = "카테고리 이름", example = "중요")
        String categoryName
) {
}
