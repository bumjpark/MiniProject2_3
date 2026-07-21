package com.example.demo.todo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.todo.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("""
            SELECT t
            FROM Todo t
            WHERE t.userId = :userId
              AND (:completed IS NULL OR t.completed = :completed)
            ORDER BY
              CASE WHEN t.completed = false THEN 0 ELSE 1 END,
              CASE WHEN t.deadline IS NULL THEN 1 ELSE 0 END,
              t.deadline ASC,
              t.todoId DESC
            """)
    List<Todo> findTodos(
            @Param("userId") Long userId,
            @Param("completed") Boolean completed
    );

    Optional<Todo> findByTodoIdAndUserId(
            Long todoId,
            Long userId
    );
}