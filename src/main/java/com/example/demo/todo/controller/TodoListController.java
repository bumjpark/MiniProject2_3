package com.example.demo.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.security.CustomUserDetails;
import com.example.demo.todo.dto.TodoListCreateRequest;
import com.example.demo.todo.dto.TodoListDto;
import com.example.demo.todo.dto.TodoListUpdateRequest;
import com.example.demo.todo.service.TodoListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/todo-lists")
@RequiredArgsConstructor
@Tag(name = "TodoList", description = "JWT 사용자별 Todo 목록 CRUD API")
@SecurityRequirement(name = "bearerAuth")
public class TodoListController {

    private final TodoListService todoListService;

    @GetMapping
    @Operation(summary = "TodoList 전체 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<List<TodoListDto>> findAll(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                todoListService.findAll(currentUserId(userDetails))
        );
    }

    @GetMapping("/{listId}")
    @Operation(summary = "TodoList 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "TodoList 없음")
    })
    public ResponseEntity<TodoListDto> findOne(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "TodoList ID", example = "1")
            @PathVariable(name = "listId") Long listId
    ) {
        return ResponseEntity.ok(
                todoListService.findOne(
                        listId,
                        currentUserId(userDetails)
                )
        );
    }

    @PostMapping
    @Operation(summary = "TodoList 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<TodoListDto> create(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TodoListCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(todoListService.create(
                        currentUserId(userDetails),
                        request
                ));
    }

    @PutMapping("/{listId}")
    @Operation(summary = "TodoList 이름 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "TodoList 없음")
    })
    public ResponseEntity<TodoListDto> update(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "TodoList ID", example = "1")
            @PathVariable(name = "listId") Long listId,
            @RequestBody TodoListUpdateRequest request
    ) {
        return ResponseEntity.ok(
                todoListService.update(
                        listId,
                        currentUserId(userDetails),
                        request
                )
        );
    }

    @DeleteMapping("/{listId}")
    @Operation(
            summary = "TodoList 삭제",
            description = "목록을 삭제해도 Todo는 유지되며 목록 연결만 해제됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "TodoList 없음")
    })
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "TodoList ID", example = "1")
            @PathVariable(name = "listId") Long listId
    ) {
        todoListService.delete(listId, currentUserId(userDetails));
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(CustomUserDetails userDetails) {
        return userDetails.getUser().getId();
    }
}
