package com.example.demo.todo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.todo.dto.TodoListCreateRequest;
import com.example.demo.todo.dto.TodoListDto;
import com.example.demo.todo.dto.TodoListUpdateRequest;
import com.example.demo.todo.entity.TodoList;
import com.example.demo.todo.repository.TodoListRepository;
import com.example.demo.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoListService {

    private final TodoListRepository todoListRepository;
    private final TodoRepository todoRepository;

    public List<TodoListDto> findAll(Long userId) {
        validateUserId(userId);

        return todoListRepository
                .findAllByUserIdOrderByListIdDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public TodoListDto findOne(Long listId, Long userId) {
        validateUserId(userId);
        return toDto(findTodoList(listId, userId));
    }

    @Transactional
    public TodoListDto create(
            Long userId,
            TodoListCreateRequest request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "요청 데이터가 없습니다."
            );
        }

        validateUserId(userId);
        validateListName(request.listName());

        TodoList saved = todoListRepository.save(
                new TodoList(
                        userId,
                        request.listName().trim()
                )
        );
        return toDto(saved);
    }

    @Transactional
    public TodoListDto update(
            Long listId,
            Long userId,
            TodoListUpdateRequest request
    ) {
        validateUserId(userId);

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "요청 데이터가 없습니다."
            );
        }

        validateListName(request.listName());
        TodoList todoList = findTodoList(listId, userId);
        todoList.rename(request.listName().trim());
        return toDto(todoList);
    }

    @Transactional
    public void delete(Long listId, Long userId) {
        validateUserId(userId);
        TodoList todoList = findTodoList(listId, userId);
        todoRepository.clearTodoList(listId, userId);
        todoListRepository.delete(todoList);
    }

    private TodoList findTodoList(Long listId, Long userId) {
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

    private TodoListDto toDto(TodoList todoList) {
        return new TodoListDto(
                todoList.getListId(),
                todoList.getUserId(),
                todoList.getListName()
        );
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바른 사용자 ID를 입력하세요."
            );
        }
    }

    private void validateListName(String listName) {
        if (listName == null || listName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "목록 이름은 필수입니다."
            );
        }

        if (listName.trim().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "목록 이름은 100자 이하여야 합니다."
            );
        }
    }
}
