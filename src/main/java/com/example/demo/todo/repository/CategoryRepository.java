package com.example.demo.todo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.todo.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

List<Category> findAllByOrderByCategoryNameAsc();

boolean existsByCategoryName(String categoryName);
}