package com.example.demo.todo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.todo.dto.TodoListDto;
import com.example.demo.todo.repository.TodoListRepository;

@Service
@Transactional
public class TodoListService {

    private final TodoListRepository todoListRepository;

    public TodoListService(
            TodoListRepository todoListRepository
    ) {
        this.todoListRepository = todoListRepository;
    }

    public List<TodoListDto> findAll(Long userId) {
        return todoListRepository
                .findAllByUserIdOrderByListIdDesc(userId)
                .stream()
                .map(todoList -> new TodoListDto(
                        todoList.getListId(),
                        todoList.getListName()
                ))
                .toList();
    }
}