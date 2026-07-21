package com.example.demo.todo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "todo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long todoId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "todo_completed", nullable = false)
    private boolean completed;

    public Todo(
            Long userId,
            String content,
            LocalDate deadline
    ) {
        this.userId = userId;
        this.content = content;
        this.deadline = deadline;
        this.completed = false;
    }

    public void update(
            String content,
            LocalDate deadline
    ) {
        this.content = content;
        this.deadline = deadline;
    }

    public void changeCompleted(boolean completed) {
        this.completed = completed;
    }
}
