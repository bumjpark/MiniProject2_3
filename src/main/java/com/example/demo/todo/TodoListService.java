package com.example.demo.todo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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
                        todoList.getName()
                ))
                .toList();
    }
}