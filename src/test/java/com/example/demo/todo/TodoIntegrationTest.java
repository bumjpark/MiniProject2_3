package com.example.demo.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.security.CustomUserDetails;
import com.example.demo.todo.controller.CategoryController;
import com.example.demo.todo.controller.TodoController;
import com.example.demo.todo.controller.TodoListController;
import com.example.demo.todo.dto.CategoryCreateRequest;
import com.example.demo.todo.dto.CategoryResponse;
import com.example.demo.todo.dto.TodoCreateRequest;
import com.example.demo.todo.dto.TodoListCreateRequest;
import com.example.demo.todo.dto.TodoListDto;
import com.example.demo.todo.dto.TodoListUpdateRequest;
import com.example.demo.todo.dto.TodoPageResponse;
import com.example.demo.todo.dto.TodoResponse;
import com.example.demo.todo.dto.TodoUpdateRequest;
import com.example.demo.todo.entity.Todo;
import com.example.demo.todo.entity.TodoList;
import com.example.demo.todo.exception.ErrorResponse;
import com.example.demo.todo.exception.TodoExceptionHandler;
import com.example.demo.todo.repository.TodoListRepository;
import com.example.demo.todo.repository.TodoRepository;
import com.example.demo.todo.service.CategoryService;
import com.example.demo.todo.service.TodoListService;
import com.example.demo.todo.service.TodoService;
import com.example.demo.calendar.service.CalendarService;
import com.example.demo.calendar.service.CurrentUserResolver;
import com.example.demo.calendar.service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.validation.Validation;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        TodoService.class,
        TodoListService.class,
        CategoryService.class,
        ScheduleService.class,
        CalendarService.class,
        CurrentUserResolver.class
})
@Sql("/sql/test.sql")
class TodoIntegrationTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().findAndRegisterModules();

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodoListService todoListService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private TodoService controllerTodoService;
    private TodoListService controllerTodoListService;
    private CategoryService controllerCategoryService;

    @BeforeEach
    void setUpControllerMockMvc() {
        controllerTodoService = mock(TodoService.class);
        controllerTodoListService = mock(TodoListService.class);
        controllerCategoryService = mock(CategoryService.class);

        User user = User.builder()
                .id(1001L)
                .email("aa@test.com")
                .password("1234")
                .name("ABC")
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                )
        );

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new TodoController(controllerTodoService),
                        new TodoListController(controllerTodoListService),
                        new CategoryController(controllerCategoryService)
                )
                .setControllerAdvice(new TodoExceptionHandler())
                .setCustomArgumentResolvers(
                        new AuthenticationPrincipalArgumentResolver()
                )
                .build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("사용자와 목록 및 카테고리 조건으로 Todo를 조회한다")
    void findTodos() {
        List<TodoResponse> todos =
                todoService.getTodos(1001L, false, 3001L, 2001L);

        assertThat(todos)
                .extracting(TodoResponse::todoId)
                .containsExactly(4001L);
    }

    @Test
    @DisplayName("목록과 카테고리 없이 Todo를 생성한다")
    void createTodoWithoutOptionalRelations() {
        TodoResponse created = todoService.createTodo(
                1001L,
                new TodoCreateRequest(
                        null,
                        null,
                        "새 Todo",
                        LocalDate.now().plusDays(1),
                        null
                )
        );

        assertThat(created.userId()).isEqualTo(1001L);
        assertThat(created.listId()).isNull();
        assertThat(created.categoryId()).isNull();
    }

    @Test
    @DisplayName("Todo를 수정하고 완료 및 카테고리 상태를 변경한다")
    void updateTodoStates() {
        TodoResponse updated = todoService.updateTodo(
                4001L,
                1001L,
                new TodoUpdateRequest(
                        3002L,
                        2003L,
                        "수정된 Todo",
                        LocalDate.now().plusDays(5)
                )
        );
        TodoResponse completed =
                todoService.changeCompleted(4001L, 1001L, true);
        TodoResponse detached =
                todoService.changeCategory(4001L, 1001L, null);

        assertThat(updated.listId()).isEqualTo(3002L);
        assertThat(updated.categoryId()).isEqualTo(2003L);
        assertThat(completed.completed()).isTrue();
        assertThat(detached.categoryId()).isNull();
    }

    @Test
    @DisplayName("TodoList 기본 CRUD가 동작한다")
    void manageTodoLists() {
        List<TodoListDto> lists = todoListService.findAll(1001L);
        TodoListDto found = todoListService.findOne(3001L, 1001L);
        TodoListDto created = todoListService.create(
                1001L,
                new TodoListCreateRequest("새 목록")
        );
        TodoListDto updated = todoListService.update(
                created.getListId(),
                1001L,
                new TodoListUpdateRequest("수정 목록")
        );

        assertThat(lists).hasSize(2);
        assertThat(found.getListName()).isEqualTo("사용자 1 업무");
        assertThat(updated.getListName()).isEqualTo("수정 목록");

        todoListService.delete(created.getListId(), 1001L);
        assertThat(todoListRepository.findById(created.getListId()))
                .isEmpty();
    }

    @Test
    @DisplayName("카테고리를 조회하고 생성하며 중복을 거부한다")
    void manageCategories() {
        List<CategoryResponse> categories =
                categoryService.getCategories();
        CategoryResponse created = categoryService.createCategory(
                new CategoryCreateRequest("새 카테고리")
        );

        assertThat(categories).hasSize(3);
        assertThat(created.categoryName()).isEqualTo("새 카테고리");
        assertThatThrownBy(() -> categoryService.createCategory(
                new CategoryCreateRequest("중요")
        )).isInstanceOfSatisfying(
                ResponseStatusException.class,
                exception -> assertThat(exception.getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT)
        );
    }

    @Test
    @DisplayName("다른 사용자의 Todo에는 접근할 수 없다")
    void rejectAnotherUsersTodo() {
        assertThatThrownBy(() -> todoService.deleteTodo(4005L, 1001L))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }

    @Test
    @DisplayName("TodoList를 삭제해도 Todo는 유지한다")
    void deleteListKeepsTodos() {
        todoListService.delete(3001L, 1001L);
        entityManager.flush();
        entityManager.clear();

        assertThat(todoListRepository.findById(3001L)).isEmpty();
        assertThat(todoRepository.findById(4001L))
                .get()
                .extracting(Todo::getTodoList)
                .isNull();
        assertThat(todoRepository.findById(4002L))
                .get()
                .extracting(Todo::getTodoList)
                .isNull();
    }

    @Test
    @DisplayName("Todo DTO Validation이 동작한다")
    void validateTodoRequest() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var violations = factory.getValidator().validate(
                    new TodoCreateRequest(
                            0L,
                            -1L,
                            "",
                            LocalDate.now().minusDays(1),
                            null
                    )
            );

            assertThat(violations).hasSize(4);
        }
    }

    @Test
    @DisplayName("Todo 예외를 통일된 오류 응답으로 변환한다")
    void handleTodoErrors() {
        TodoExceptionHandler handler = new TodoExceptionHandler();

        ResponseEntity<ErrorResponse> notFound =
                handler.handleResponseStatusException(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Todo가 없습니다."
                        )
                );
        ResponseEntity<ErrorResponse> noSuchElement =
                handler.handleNoSuchElementException(
                        new NoSuchElementException("목록이 없습니다.")
                );
        ResponseEntity<ErrorResponse> illegalArgument =
                handler.handleIllegalArgumentException(
                        new IllegalArgumentException("잘못된 값입니다.")
                );
        ResponseEntity<ErrorResponse> invalidRequest =
                handler.handleInvalidRequest(new RuntimeException());
        ResponseEntity<ErrorResponse> unexpected =
                handler.handleException(new RuntimeException());

        assertThat(notFound.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(notFound.getBody().getMessage())
                .isEqualTo("Todo가 없습니다.");
        assertThat(noSuchElement.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(illegalArgument.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(invalidRequest.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(unexpected.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("없는 사용자로 Todo를 저장하면 FK 오류가 발생한다")
    void rejectTodoWithMissingUser() {
        Todo todo = new Todo(
                999999L,
                null,
                null,
                "잘못된 사용자 Todo",
                LocalDate.now().plusDays(1)
        );

        assertThatThrownBy(() -> todoRepository.saveAndFlush(todo))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("없는 사용자로 TodoList를 저장하면 FK 오류가 발생한다")
    void rejectTodoListWithMissingUser() {
        TodoList todoList = new TodoList(999999L, "잘못된 사용자 목록");

        assertThatThrownBy(() -> todoListRepository.saveAndFlush(todoList))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("MockMvc로 Todo 페이징 조회 API를 검증한다")
    void getTodosWithMockMvc() throws Exception {
        TodoResponse todo = todoResponse(false);
        TodoPageResponse pageResponse = TodoPageResponse.from(
                new PageImpl<>(
                        List.of(todo),
                        PageRequest.of(0, 20),
                        1
                )
        );

        given(controllerTodoService.getTodos(
                1001L,
                false,
                3001L,
                2001L,
                0,
                20
        )).willReturn(pageResponse);

        mockMvc.perform(get("/api/todos")
                        .param("completed", "false")
                        .param("listId", "3001")
                        .param("categoryId", "2001")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].todoId").value(4001))
                .andExpect(jsonPath("$.content[0].content").value("중요 업무 마무리"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));

        verify(controllerTodoService).getTodos(
                1001L,
                false,
                3001L,
                2001L,
                0,
                20
        );
    }

    @Test
    @DisplayName("MockMvc로 Todo 생성 수정 완료 카테고리 삭제 API를 검증한다")
    void manageTodosWithMockMvc() throws Exception {
        TodoResponse active = todoResponse(false);
        TodoResponse completed = todoResponse(true);

        given(controllerTodoService.createTodo(
                eq(1001L),
                any(TodoCreateRequest.class)
        )).willReturn(active);
        given(controllerTodoService.updateTodo(
                eq(4001L),
                eq(1001L),
                any(TodoUpdateRequest.class)
        )).willReturn(active);
        given(controllerTodoService.changeCompleted(
                4001L,
                1001L,
                true
        )).willReturn(completed);
        given(controllerTodoService.changeCategory(
                4001L,
                1001L,
                2001L
        )).willReturn(active);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "listId": 3001,
                                  "categoryId": 2001,
                                  "content": "중요 업무 마무리",
                                  "deadline": "%s",
                                  "calendarId": null
                                }
                                """.formatted(LocalDate.now().plusDays(3))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.todoId").value(4001));

        mockMvc.perform(put("/api/todos/4001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "listId": 3001,
                                  "categoryId": 2001,
                                  "content": "중요 업무 마무리",
                                  "deadline": "%s"
                                }
                                """.formatted(LocalDate.now().plusDays(3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("중요 업무 마무리"));

        mockMvc.perform(patch("/api/todos/4001/completed")
                        .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));

        mockMvc.perform(patch("/api/todos/4001/category")
                        .param("categoryId", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(2001));

        mockMvc.perform(delete("/api/todos/4001"))
                .andExpect(status().isNoContent());

        verify(controllerTodoService).deleteTodo(4001L, 1001L);
    }

    @Test
    @DisplayName("MockMvc로 TodoList와 Category API를 검증한다")
    void manageTodoListsAndCategoriesWithMockMvc() throws Exception {
        TodoListDto list = new TodoListDto(3001L, 1001L, "사용자 1 업무");
        CategoryResponse category = new CategoryResponse(2001L, "중요");

        given(controllerTodoListService.findAll(1001L))
                .willReturn(List.of(list));
        given(controllerTodoListService.findOne(3001L, 1001L))
                .willReturn(list);
        given(controllerTodoListService.create(
                eq(1001L),
                any(TodoListCreateRequest.class)
        )).willReturn(list);
        given(controllerTodoListService.update(
                eq(3001L),
                eq(1001L),
                any(TodoListUpdateRequest.class)
        )).willReturn(list);
        given(controllerCategoryService.getCategories())
                .willReturn(List.of(category));
        given(controllerCategoryService.createCategory(
                any(CategoryCreateRequest.class)
        )).willReturn(category);

        mockMvc.perform(get("/api/todo-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].listId").value(3001));

        mockMvc.perform(get("/api/todo-lists/3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listName").value("사용자 1 업무"));

        mockMvc.perform(post("/api/todo-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"listName\":\"사용자 1 업무\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/todo-lists/3001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"listName\":\"사용자 1 업무\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/todo-lists/3001"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("중요"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"중요\"}"))
                .andExpect(status().isCreated());

        verify(controllerTodoListService).delete(3001L, 1001L);
    }

    private TodoResponse todoResponse(boolean completed) {
        return new TodoResponse(
                4001L,
                1001L,
                3001L,
                "사용자 1 업무",
                2001L,
                "중요",
                "중요 업무 마무리",
                LocalDate.now().plusDays(3),
                completed,
                3L,
                null
        );
    }
}
