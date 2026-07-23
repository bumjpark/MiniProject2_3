package com.example.demo.todo.entity;

import java.time.LocalDate;

import com.example.demo.auth.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_todo_user")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    private TodoList todoList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "todo_completed", nullable = false)
    private boolean completed;

    // 생성 시 캘린더에도 일정을 같이 만들었다면, 그 Schedule의 id (calendar 패키지 소유, FK 매핑 없이 순수 참조)
    @Column(name = "schedule_id")
    private Long scheduleId;

    public Todo(
            Long userId,
            TodoList todoList,
            Category category,
            String content,
            LocalDate deadline
    ) {
        this.userId = userId;
        this.todoList = todoList;
        this.category = category;
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

    public void moveTo(TodoList todoList) {
        this.todoList = todoList;
    }

    public void changeCategory(Category category) {
        this.category = category;
    }

    public void linkSchedule(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
}
