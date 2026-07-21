package com.example.demo.todo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.todo.dto.TodoCreateRequest;
import com.example.demo.todo.dto.TodoResponse;
import com.example.demo.todo.dto.TodoUpdateRequest;
import com.example.demo.todo.entity.Todo;
import com.example.demo.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    public List<TodoResponse> getTodos(
            Long userId,
            Boolean completed
    ) {
        validateUserId(userId);

        return todoRepository.findTodos(userId, completed)
                .stream()
                .map(TodoResponse::from)
                .toList();
    }

    @Transactional
    public TodoResponse createTodo(
            TodoCreateRequest request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "요청 데이터가 없습니다."
            );
        }

        validateUserId(request.userId());
        validateContent(request.content());

        Todo todo = new Todo(
                request.userId(),
                request.content().trim(),
                request.deadline()
        );

        Todo savedTodo = todoRepository.save(todo);

        return TodoResponse.from(savedTodo);
    }

    @Transactional
    public TodoResponse updateTodo(
            Long todoId,
            Long userId,
            TodoUpdateRequest request
    ) {
        validateUserId(userId);

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "요청 데이터가 없습니다."
            );
        }

        validateContent(request.content());

        Todo todo = findTodo(todoId, userId);

        todo.update(
                request.content().trim(),
                request.deadline()
        );
        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse changeCompleted(
            Long todoId,
            Long userId,
            boolean completed
    ) {
        validateUserId(userId);

        Todo todo = findTodo(todoId, userId);

        todo.changeCompleted(completed);

        return TodoResponse.from(todo);
    }

    @Transactional
    public void deleteTodo(
            Long todoId,
            Long userId
    ) {
        validateUserId(userId);

        Todo todo = findTodo(todoId, userId);

        todoRepository.delete(todo);
    }

    private Todo findTodo(
            Long todoId,
            Long userId
    ) {
        if (todoId == null || todoId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바른 Todo ID를 입력하세요."
            );
        }

        return todoRepository
                .findByTodoIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Todo를 찾을 수 없습니다."
                ));
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바른 사용자 ID를 입력하세요."
            );
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Todo 내용은 필수입니다."
            );
        }

        if (content.trim().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Todo 내용은 255자 이하여야 합니다."
            );
        }
    }
}