package com.example.demo.todo.dto;

import java.time.LocalDate;

public record TodoCreateRequest(
        Long userId,
        String content,
        LocalDate deadline
) {
}