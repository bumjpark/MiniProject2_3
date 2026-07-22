package com.example.demo.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.todo.dto.TodoListCreateRequest;
import com.example.demo.todo.dto.TodoListDto;
import com.example.demo.todo.dto.TodoListUpdateRequest;
import com.example.demo.todo.service.TodoListService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/todo-lists")
@RequiredArgsConstructor
public class TodoListController {

    private final TodoListService todoListService;

    @GetMapping
    public ResponseEntity<List<TodoListDto>> findAll(
            @RequestParam(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(todoListService.findAll(userId));
    }

    @GetMapping("/{listId}")
    public ResponseEntity<TodoListDto> findOne(
            @PathVariable(name = "listId") Long listId,
            @RequestParam(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(todoListService.findOne(listId, userId));
    }

    @PostMapping
    public ResponseEntity<TodoListDto> create(
            @RequestBody TodoListCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(todoListService.create(request));
    }

    @PutMapping("/{listId}")
    public ResponseEntity<TodoListDto> update(
            @PathVariable(name = "listId") Long listId,
            @RequestParam(name = "userId") Long userId,
            @RequestBody TodoListUpdateRequest request
    ) {
        return ResponseEntity.ok(
                todoListService.update(listId, userId, request)
        );
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> delete(
            @PathVariable(name = "listId") Long listId,
            @RequestParam(name = "userId") Long userId
    ) {
        todoListService.delete(listId, userId);
        return ResponseEntity.noContent().build();
    }
}
