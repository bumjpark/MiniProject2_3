package com.example.demo.todo.dto;

import java.time.LocalDate;

public record TodoCreateRequest(
        Long userId,
        Long listId,
        String content,
        LocalDate deadline
) {
}
