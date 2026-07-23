package com.example.demo.todo.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.example.demo.todo.entity.Todo;
import com.example.demo.todo.entity.TodoList;
import com.example.demo.todo.entity.Category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Todo 응답")
public record TodoResponse(
        @Schema(description = "Todo ID", example = "1")
        Long todoId,
        @Schema(description = "사용자 ID", example = "1")
        Long userId,
        @Schema(description = "연결된 TodoList ID", example = "1")
        Long listId,
        @Schema(description = "연결된 TodoList 이름", example = "업무")
        String listName,
        @Schema(description = "연결된 카테고리 ID", example = "1")
        Long categoryId,
        @Schema(description = "연결된 카테고리 이름", example = "중요")
        String categoryName,
        @Schema(description = "Todo 내용", example = "프로젝트 발표 자료 준비")
        String content,
        @Schema(description = "마감일", example = "2026-07-30")
        LocalDate deadline,
        @Schema(description = "완료 여부", example = "false")
        boolean completed,
        @Schema(description = "오늘부터 마감일까지 남은 일수", example = "8")
        Long remainingDays,
        @Schema(description = "연결된 캘린더의 일정(Schedule) ID. 연결된 일정이 없으면 null", example = "1")
        Long scheduleId
) {

    public static TodoResponse from(Todo todo) {
        Long remainingDays = null;
        TodoList todoList = todo.getTodoList();
        Category category = todo.getCategory();

        if (todo.getDeadline() != null) {
            remainingDays = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    todo.getDeadline()
            );
        }

        return new TodoResponse(
                todo.getTodoId(),
                todo.getUserId(),
                todoList == null ? null : todoList.getListId(),
                todoList == null ? null : todoList.getListName(),
                category == null ? null : category.getCategoryId(),
                category == null ? null : category.getCategoryName(),
                todo.getContent(),
                todo.getDeadline(),
                todo.isCompleted(),
                remainingDays,
                todo.getScheduleId()
        );
    }
}
