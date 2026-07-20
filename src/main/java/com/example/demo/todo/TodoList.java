package com.example.demo.todo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "todo_list")
public class TodoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private Long listId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    protected TodoList() {
    }

    // 테스트 데이터 생성용
    public TodoList(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Long getListId() {
        return listId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}