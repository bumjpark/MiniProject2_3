package com.example.demo.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.todo.dto.TodoCreateRequest;
import com.example.demo.todo.dto.TodoResponse;
import com.example.demo.todo.dto.TodoUpdateRequest;
import com.example.demo.todo.service.TodoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getTodos(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(
                    name = "completed",
                    required = false
            ) Boolean completed
    ) {
        return ResponseEntity.ok(
                todoService.getTodos(userId, completed)
        );
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(
            @RequestBody TodoCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(todoService.createTodo(request));
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable(name = "todoId") Long todoId,
            @RequestParam(name = "userId") Long userId,
            @RequestBody TodoUpdateRequest request
    ) {
        return ResponseEntity.ok(
                todoService.updateTodo(
                        todoId,
                        userId,
                        request
                )
        );
    }

    @PatchMapping("/{todoId}/completed")
    public ResponseEntity<TodoResponse> changeCompleted(
            @PathVariable(name = "todoId") Long todoId,
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "completed") boolean completed
    ) {
        return ResponseEntity.ok(
                todoService.changeCompleted(
                        todoId,
                        userId,
                        completed
                )
        );
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable(name = "todoId") Long todoId,
            @RequestParam(name = "userId") Long userId
    ) {
        todoService.deleteTodo(todoId, userId);
        return ResponseEntity.noContent().build();
    }
}