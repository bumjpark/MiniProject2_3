package com.example.demo.todo.dto;

public record TodoListCreateRequest(
        Long userId,
        String listName
) {
}
