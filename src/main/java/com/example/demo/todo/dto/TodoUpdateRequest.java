package com.example.demo.todo.dto;

import java.time.LocalDate;

public record TodoUpdateRequest(
        String content,
        LocalDate deadline
) {
}