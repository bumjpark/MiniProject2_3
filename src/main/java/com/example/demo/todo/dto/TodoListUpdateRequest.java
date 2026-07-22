package com.example.demo.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "TodoList 수정 요청")
public record TodoListUpdateRequest(
        @Schema(description = "변경할 목록 이름", example = "개인 업무")
        @NotBlank(message = "목록 이름은 필수입니다.")
        @Size(max = 100, message = "목록 이름은 100자 이하여야 합니다.")
        String listName
) {
}
