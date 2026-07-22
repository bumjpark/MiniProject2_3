package com.example.demo.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "TodoList 수정 요청")
public record TodoListUpdateRequest(
        @Schema(description = "변경할 목록 이름", example = "개인 업무")
        String listName
) {
}
