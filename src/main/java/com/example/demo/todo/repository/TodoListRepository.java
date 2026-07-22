package com.example.demo.todo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.todo.entity.TodoList;

public interface TodoListRepository
        extends JpaRepository<TodoList, Long> {

    List<TodoList> findAllByUserIdOrderByListIdDesc(Long userId);

    Optional<TodoList> findByListIdAndUserId(Long listId, Long userId);
}
