package com.example.demo.todo;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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