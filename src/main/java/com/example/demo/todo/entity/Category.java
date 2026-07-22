package com.example.demo.todo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(
            name = "category_name",
            nullable = false,
            unique = true,
            length = 50
    )
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Todo> todos = new ArrayList<>();

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
