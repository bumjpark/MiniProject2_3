package com.example.demo.todo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.calendar.dto.ScheduleRequest;
import com.example.demo.calendar.entity.Schedule;
import com.example.demo.calendar.repository.ScheduleRepository;
import com.example.demo.calendar.service.ScheduleService;
import com.example.demo.todo.dto.TodoCreateRequest;
import com.example.demo.todo.dto.TodoPageResponse;
import com.example.demo.todo.dto.TodoResponse;
import com.example.demo.todo.dto.TodoUpdateRequest;
import com.example.demo.todo.entity.Category;
import com.example.demo.todo.entity.Todo;
import com.example.demo.todo.entity.TodoList;
import com.example.demo.todo.repository.CategoryRepository;
import com.example.demo.todo.repository.TodoListRepository;
import com.example.demo.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private static final Set<Integer> ALLOWED_PAGE_SIZES =
            Set.of(10, 20, 50, 100);

    private final TodoRepository todoRepository;
    private final TodoListRepository todoListRepository;
    private final CategoryRepository categoryRepository;
    private final ScheduleService scheduleService;
    private final ScheduleRepository scheduleRepository;

    public List<TodoResponse> getTodos(
            Long userId,
            Boolean completed
    ) {
        return getTodos(userId, completed, null, null);
    }

    public List<TodoResponse> getTodos(
            Long userId,
            Boolean completed,
            Long listId
    ) {
        return getTodos(userId, completed, listId, null);
    }

    public List<TodoResponse> getTodos(
            Long userId,
            Boolean completed,
            Long listId,
            Long categoryId
    ) {
        return findTodos(
                        userId,
                        completed,
                        listId,
                        categoryId,
                        Pageable.unpaged()
                )
                .stream()
                .map(TodoResponse::from)
                .toList();
    }

    public TodoPageResponse getTodos(
            Long userId,
            Boolean completed,
            Long listId,
            Long categoryId,
            int page,
            int size
    ) {
        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "페이지 번호는 0 이상이어야 합니다."
            );
        }

        if (!ALLOWED_PAGE_SIZES.contains(size)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "페이지 크기는 10, 20, 50, 100 중 하나여야 합니다."
            );
        }

        Page<TodoResponse> todoPage = findTodos(
                userId,
                completed,
                listId,
                categoryId,
                PageRequest.of(page, size)
        ).map(TodoResponse::from);

        return TodoPageResponse.from(todoPage);
    }

    private Page<Todo> findTodos(
            Long userId,
            Boolean completed,
            Long listId,
            Long categoryId,
            Pageable pageable
    ) {
        validateUserId(userId);

        if (listId != null) {
            findTodoList(listId, userId);
        }

        if (categoryId != null) {
            findCategory(categoryId);
        }

        return todoRepository.findTodos(
                userId,
                completed,
                listId,
                categoryId,
                pageable
        );
    }

    @Transactional
    public TodoResponse createTodo(
            Long userId,
            TodoCreateRequest request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "요청 데이터가 없습니다."
            );
        }

        validateUserId(userId);
        validateContent(request.content());
        TodoList todoList = request.listId() == null
                ? null
                : findTodoList(request.listId(), userId);
        Category category = request.categoryId() == null
                ? null
                : findCategory(request.categoryId());

        Todo todo = new Todo(
                userId,
                todoList,
                category,
                request.content().trim(),
                request.deadline()
        );

        if (request.calendarId() != null) {
            Schedule schedule = createLinkedSchedule(
                    request.calendarId(),
                    request.content().trim(),
                    request.deadline()
            );
            todo.linkSchedule(schedule.getScheduleId());
        }

        Todo savedTodo = todoRepository.save(todo);

        return TodoResponse.from(savedTodo);
    }

    // Todo와 같이 만들 캘린더 일정을 생성한다. 캘린더 멤버십 검증은 ScheduleService.create()가 대신 해준다.
    private Schedule createLinkedSchedule(Long calendarId, String title, LocalDate deadline) {
        LocalDateTime start = deadline == null ? null : deadline.atStartOfDay();
        LocalDateTime end = start == null ? null : start.plusHours(1);

        ScheduleRequest scheduleRequest = new ScheduleRequest(title, start, end, null);
        return scheduleService.create(calendarId, scheduleRequest);
    }

    @Transactional
    public TodoResponse updateTodo(
            Long todoId,
            Long userId,
            TodoUpdateRequest request
    ) {
        validateUserId(userId);

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "요청 데이터가 없습니다."
            );
        }

        validateContent(request.content());

        Todo todo = findTodo(todoId, userId);

        if (request.listId() != null) {
            todo.moveTo(findTodoList(request.listId(), userId));
        }

        if (request.categoryId() != null) {
            todo.changeCategory(findCategory(request.categoryId()));
        }

        todo.update(
                request.content().trim(),
                request.deadline()
        );
        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse changeCompleted(
            Long todoId,
            Long userId,
            boolean completed
    ) {
        validateUserId(userId);

        Todo todo = findTodo(todoId, userId);

        todo.changeCompleted(completed);

        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse changeCategory(
            Long todoId,
            Long userId,
            Long categoryId
    ) {
        validateUserId(userId);

        Todo todo = findTodo(todoId, userId);
        Category category = categoryId == null
                ? null
                : findCategory(categoryId);

        todo.changeCategory(category);
        return TodoResponse.from(todo);
    }

    @Transactional
    public void deleteTodo(
            Long todoId,
            Long userId
    ) {
        validateUserId(userId);

        Todo todo = findTodo(todoId, userId);

        // 연결된 캘린더 일정이 있다면 같이 지운다. 이미 이 Todo를 지우는 중이므로 멤버십 재검증 없이 직접 삭제.
        if (todo.getScheduleId() != null) {
            scheduleRepository.deleteById(todo.getScheduleId());
        }

        todoRepository.delete(todo);
    }

    private Todo findTodo(
            Long todoId,
            Long userId
    ) {
        if (todoId == null || todoId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바른 Todo ID를 입력하세요."
            );
        }

        return todoRepository
                .findByTodoIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Todo를 찾을 수 없습니다."
                ));
    }

    private TodoList findTodoList(
            Long listId,
            Long userId
    ) {
        if (listId == null || listId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바른 목록 ID를 입력하세요."
            );
        }

        return todoListRepository
                .findByListIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 사용자의 Todo 목록을 찾을 수 없습니다."
                ));
    }

    private Category findCategory(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바른 카테고리 ID를 입력하세요."
            );
        }

        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "카테고리를 찾을 수 없습니다."
                ));
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바른 사용자 ID를 입력하세요."
            );
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Todo 내용은 필수입니다."
            );
        }

        if (content.trim().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Todo 내용은 255자 이하여야 합니다."
            );
        }
    }
}
