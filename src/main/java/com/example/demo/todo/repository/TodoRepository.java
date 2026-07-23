package com.example.demo.todo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.todo.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query(
            value = """
            SELECT t
            FROM Todo t
            LEFT JOIN FETCH t.todoList l
            LEFT JOIN FETCH t.category c
            WHERE t.userId = :userId
              AND (:completed IS NULL OR t.completed = :completed)
              AND (:listId IS NULL OR l.listId = :listId)
              AND (:categoryId IS NULL OR c.categoryId = :categoryId)
            ORDER BY
              CASE WHEN t.completed = false THEN 0 ELSE 1 END,
              CASE WHEN t.deadline IS NULL THEN 1 ELSE 0 END,
              t.deadline ASC,
              t.todoId DESC
            """,
            countQuery = """
            SELECT COUNT(t)
            FROM Todo t
            LEFT JOIN t.todoList l
            LEFT JOIN t.category c
            WHERE t.userId = :userId
              AND (:completed IS NULL OR t.completed = :completed)
              AND (:listId IS NULL OR l.listId = :listId)
              AND (:categoryId IS NULL OR c.categoryId = :categoryId)
            """
    )
    Page<Todo> findTodos(
            @Param("userId") Long userId,
            @Param("completed") Boolean completed,
            @Param("listId") Long listId,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    Optional<Todo> findByTodoIdAndUserId(
            Long todoId,
            Long userId
    );

    Optional<Todo> findByScheduleId(Long scheduleId);

    // 연결된 Schedule이 지워져도 Todo 자체는 남기고 연결만 끊는다.
    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Todo t
            SET t.scheduleId = NULL
            WHERE t.scheduleId IN :scheduleIds
            """)
    void unlinkByScheduleIdIn(@Param("scheduleIds") List<Long> scheduleIds);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Todo t
            SET t.todoList = NULL
            WHERE t.userId = :userId
              AND t.todoList.listId = :listId
            """)
    int clearTodoList(
            @Param("listId") Long listId,
            @Param("userId") Long userId
    );
}
