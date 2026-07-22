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
import com.example.demo.todo.entity.TodoList;
import com.example.demo.todo.repository.TodoListRepository;
import com.example.demo.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoListRepository todoListRepository;

    public List<TodoResponse> getTodos(
            Long userId,
            Boolean completed
    ) {
        return getTodos(userId, completed, null);
    }

    public List<TodoResponse> getTodos(
            Long userId,
            Boolean completed,
            Long listId
    ) {
        validateUserId(userId);

        if (listId != null) {
            findTodoList(listId, userId);
        }

        return todoRepository.findTodos(userId, completed, listId)
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
        TodoList todoList = request.listId() == null
                ? null
                : findTodoList(request.listId(), request.userId());

        Todo todo = new Todo(
                request.userId(),
                todoList,
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

        if (request.listId() != null) {
            todo.moveTo(findTodoList(request.listId(), userId));
        }

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

    private TodoList findTodoList(
            Long listId,
            Long userId
    ) {
        if (listId == null || listId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바른 목록 ID를 입력하세요."
            );
        }

        return todoListRepository
                .findByListIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 사용자의 Todo 목록을 찾을 수 없습니다."
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
