package com.example.demo.todo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.todo.dto.TodoListDto;
import com.example.demo.todo.service.TodoListService;

@RestController
@RequestMapping("/api/todo-lists")
public class TodoListController {

    private static final Long TEST_USER_ID = 1L;

    private final TodoListService todoListService;

    public TodoListController(
            TodoListService todoListService
    ) {
        this.todoListService = todoListService;
    }

    @GetMapping
    public List<TodoListDto> findAll() {
        return todoListService.findAll(TEST_USER_ID);
    }
}