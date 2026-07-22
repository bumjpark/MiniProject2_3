package com.example.demo.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "TodoList 생성 요청")
public record TodoListCreateRequest(
        @Schema(description = "목록 이름", example = "업무")
        String listName
) {
}
