package com.example.demo.todo.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.example.demo.todo.entity.Todo;

public record TodoResponse(
        Long todoId,
        Long userId,
        String content,
        LocalDate deadline,
        boolean completed,
        Long remainingDays
) {

    public static TodoResponse from(Todo todo) {
        Long remainingDays = null;

        if (todo.getDeadline() != null) {
            remainingDays = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    todo.getDeadline()
            );
        }

        return new TodoResponse(
                todo.getTodoId(),
                todo.getUserId(),
                todo.getContent(),
                todo.getDeadline(),
                todo.isCompleted(),
                remainingDays
        );
    }
}