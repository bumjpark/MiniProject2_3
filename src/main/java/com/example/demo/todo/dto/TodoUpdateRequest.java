package com.example.demo.todo.dto;

import java.time.LocalDate;

public record TodoUpdateRequest(
        Long listId,
        String content,
        LocalDate deadline
) {
}
