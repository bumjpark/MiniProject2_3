package com.example.demo.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.security.CustomUserDetails;
import com.example.demo.todo.dto.TodoCreateRequest;
import com.example.demo.todo.dto.TodoResponse;
import com.example.demo.todo.dto.TodoUpdateRequest;
import com.example.demo.todo.service.TodoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "Todo", description = "Todo 생성, 조회, 수정, 완료 처리, 카테고리 연결 및 삭제 API")
@SecurityRequirement(name = "bearerAuth")
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    @Operation(
            summary = "Todo 목록 조회",
            description = "JWT 사용자의 Todo를 완료 상태, TodoList, 카테고리 조건으로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<List<TodoResponse>> getTodos(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "완료 여부. 생략하면 전체 조회", example = "false")
            @RequestParam(name = "completed", required = false) Boolean completed,
            @Parameter(description = "TodoList ID. 생략하면 모든 목록 조회", example = "1")
            @RequestParam(name = "listId", required = false) Long listId,
            @Parameter(description = "카테고리 ID. 생략하면 모든 카테고리 조회", example = "1")
            @RequestParam(name = "categoryId", required = false) Long categoryId
    ) {
        return ResponseEntity.ok(
                todoService.getTodos(
                        currentUserId(userDetails),
                        completed,
                        listId,
                        categoryId
                )
        );
    }

    @PostMapping
    @Operation(summary = "Todo 생성", description = "TodoList와 카테고리는 선택 사항입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "TodoList 또는 카테고리 없음")
    })
    public ResponseEntity<TodoResponse> createTodo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TodoCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(todoService.createTodo(
                        currentUserId(userDetails),
                        request
                ));
    }

    @PutMapping("/{todoId}")
    @Operation(
            summary = "Todo 수정",
            description = "내용과 마감일을 수정하고 전달된 TodoList 또는 카테고리로 이동합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "관련 데이터 없음")
    })
    public ResponseEntity<TodoResponse> updateTodo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Todo ID", example = "1")
            @PathVariable(name = "todoId") Long todoId,
            @Valid @RequestBody TodoUpdateRequest request
    ) {
        return ResponseEntity.ok(
                todoService.updateTodo(
                        todoId,
                        currentUserId(userDetails),
                        request
                )
        );
    }

    @PatchMapping("/{todoId}/completed")
    @Operation(summary = "Todo 완료 상태 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "404", description = "Todo 없음")
    })
    public ResponseEntity<TodoResponse> changeCompleted(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Todo ID", example = "1")
            @PathVariable(name = "todoId") Long todoId,
            @Parameter(description = "변경할 완료 여부", example = "true")
            @RequestParam(name = "completed") boolean completed
    ) {
        return ResponseEntity.ok(
                todoService.changeCompleted(
                        todoId,
                        currentUserId(userDetails),
                        completed
                )
        );
    }

    @PatchMapping("/{todoId}/category")
    @Operation(
            summary = "Todo 카테고리 변경",
            description = "categoryId를 생략하면 카테고리 연결을 해제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "404", description = "Todo 또는 카테고리 없음")
    })
    public ResponseEntity<TodoResponse> changeCategory(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Todo ID", example = "1")
            @PathVariable(name = "todoId") Long todoId,
            @Parameter(description = "카테고리 ID. 생략하면 연결 해제", example = "1")
            @RequestParam(name = "categoryId", required = false) Long categoryId
    ) {
        return ResponseEntity.ok(
                todoService.changeCategory(
                        todoId,
                        currentUserId(userDetails),
                        categoryId
                )
        );
    }

    @DeleteMapping("/{todoId}")
    @Operation(summary = "Todo 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "Todo 없음")
    })
    public ResponseEntity<Void> deleteTodo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Todo ID", example = "1")
            @PathVariable(name = "todoId") Long todoId
    ) {
        todoService.deleteTodo(todoId, currentUserId(userDetails));
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(CustomUserDetails userDetails) {
        return userDetails.getUser().getId();
    }
}
