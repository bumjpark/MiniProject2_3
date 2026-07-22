package com.example.demo.todo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.todo.dto.CategoryCreateRequest;
import com.example.demo.todo.dto.CategoryResponse;
import com.example.demo.todo.entity.Category;
import com.example.demo.todo.repository.CategoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getCategories() {
        return categoryRepository
                .findAllByOrderByCategoryNameAsc()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(
            CategoryCreateRequest request
    ) {
        if (request == null
                || request.categoryName() == null
                || request.categoryName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "카테고리 이름은 필수입니다."
            );
        }

        String categoryName =
                request.categoryName().trim();

        if (categoryName.length() > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "카테고리 이름은 50자 이하여야 합니다."
            );
        }

        if (categoryRepository
                .existsByCategoryName(categoryName)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 존재하는 카테고리입니다."
            );
        }

        Category category =
                categoryRepository.save(
                        new Category(categoryName)
                );

        return CategoryResponse.from(category);
    }
}