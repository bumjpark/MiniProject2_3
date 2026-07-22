package com.example.demo.todo.dto;

import com.example.demo.todo.entity.Category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 응답")
public record CategoryResponse(
        @Schema(description = "카테고리 ID", example = "1")
        Long categoryId,
        @Schema(description = "카테고리 이름", example = "중요")
        String categoryName
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getCategoryName()
        );
    }
}
