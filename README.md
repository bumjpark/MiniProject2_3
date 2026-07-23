###To-Do & Shared Calendar

> 개인 To-Do 관리와 팀원 간 일정 공유를 위한 캘린더 서비스

---

# 📌 프로젝트 소개

To-Do 리스트와 공유 캘린더를 결합한 일정 관리 서비스입니다.

- 개인 To-Do를 관리할 수 있습니다.
- 여러 사용자가 하나의 캘린더를 공유할 수 있습니다.
- 캘린더에서 일정을 생성 및 관리할 수 있습니다.
- JWT 기반 인증을 통해 안전하게 서비스를 이용할 수 있습니다.

---

# 🛠 기술 스택

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- MySQL
- Gradle
- Lombok

## Frontend

- HTML
- CSS
- JavaScript

## Database

- MySQL

## Tools

- Git
- GitHub
- Postman
- STS4

---
# 📂 프로젝트 구조

<details>
<summary>📁 프로젝트 구조 보기</summary>

```text
src/main/java/com/example/demo
├── MiniProject23Application.java
│
├── auth (인증/인가 및 사용자 관리)
│   ├── config
│   │   └── SecurityConfig.java
│   ├── controller
│   │   ├── AuthController.java
│   │   └── UserController.java
│   ├── dto
│   │   ├── LoginRequestDto.java
│   │   ├── LoginResponseDto.java
│   │   ├── SignupRequestDto.java
│   │   ├── TokenRefreshRequestDto.java
│   │   ├── TokenRefreshResponseDto.java
│   │   └── UserResponseDto.java
│   ├── entity
│   │   └── User.java
│   ├── repository
│   │   └── UserRepository.java
│   ├── security
│   │   ├── CustomUserDetails.java
│   │   ├── JwtFilter.java
│   │   └── JwtUtil.java
│   └── service
│       ├── CustomUserDetailsService.java
│       ├── UserService.java
│       └── UserServiceImpl.java
│
├── calendar (공유 캘린더 기능)
│   ├── controller
│   │   └── CalendarController.java
│   ├── dto
│   │   ├── CalendarRequestDto.java
│   │   ├── CalendarResponseDto.java
│   │   └── SharedUserDto.java
│   ├── entity
│   │   ├── Calendar.java
│   │   └── SharedCalendar.java
│   ├── repository
│   │   ├── CalendarRepository.java
│   │   └── SharedCalendarRepository.java
│   └── service
│       ├── CalendarService.java
│       └── CalendarServiceImpl.java
│
└── todo (개인 To-Do 및 카테고리 관리)
    ├── controller
    │   ├── CategoryController.java
    │   ├── TodoController.java
    │   └── TodoListController.java
    ├── dto
    │   ├── CategoryDto.java
    │   ├── TodoDto.java
    │   └── TodoListDto.java
    ├── entity
    │   ├── Category.java
    │   ├── Todo.java
    │   └── TodoList.java
    ├── repository
    │   ├── CategoryRepository.java
    │   ├── TodoListRepository.java
    │   └── TodoRepository.java
    └── service
        ├── CategoryService.java
        ├── CategoryServiceImpl.java
        ├── TodoListService.java
        ├── TodoListServiceImpl.java
        ├── TodoService.java
        └── TodoServiceImpl.java
```

</details>
---

# ✨ 주요 기능

## 🔐 회원

- 회원가입
- 로그인
- 로그아웃
- JWT Access Token 발급
- Refresh Token 재발급

---

## ✅ To-Do

- To-Do 생성
- To-Do 조회
- To-Do 수정
- To-Do 삭제

---

## 📅 Calendar

- 캘린더 생성
- 일정 등록
- 일정 수정
- 일정 삭제
- 공유 캘린더


---

# 🗄 ERD

https://lg4hq.slack.com/files/U0AR3EX3JUU/F0BJM6TQEEA/_______________________________2026-07-21______________3.28.10.png

---


# 📌 API

<details>
<summary> 회원 API</summary>

<br>

| Method | URL | 설명 |
|---------|-----|------|
| POST | /users/signup | 회원가입 |
| POST | /users/login | 로그인 |
| POST | /users/logout | 로그아웃 |
| POST | /users/refresh | Access Token 재발급 |

</details>

---

<details>
<summary>캘린더 API</summary>

<br>

| Method | URL | 설명 |
|---------|-----|------|
| GET | /calendars | 캘린더 전체 조회 |
| POST | /calendars | 캘린더 생성 |
| GET | /calendars/{calendarId} | 캘린더 단건 조회 |
| PUT | /calendars/{calendarId} | 캘린더 수정 |
| DELETE | /calendars/{calendarId} | 캘린더 삭제 |

</details>

---

<details>
<summary> 캘린더 멤버 API</summary>

<br>

| Method | URL | 설명 |
|---------|-----|------|
| POST | /calendars/{calendarId}/members/{userId} | 캘린더 멤버 추가 |
| DELETE | /calendars/{calendarId}/members/{userId} | 캘린더 멤버 제거 |

</details>

---

<details>
<summary> 일정(Schedule) API</summary>

<br>

| Method | URL | 설명 |
|---------|-----|------|
| GET | /calendars/{calendarId}/schedules | 일정 전체 조회 |
| POST | /calendars/{calendarId}/schedules | 일정 생성 |
| GET | /calendars/{calendarId}/schedules/{scheduleId} | 일정 단건 조회 |
| PUT | /calendars/{calendarId}/schedules/{scheduleId} | 일정 수정 |
| DELETE | /calendars/{calendarId}/schedules/{scheduleId} | 일정 삭제 |

</details>

---

<details>
<summary> 카테고리(Category) API</summary>

<br>

| Method | URL | 설명 |
|---------|-----|------|
| GET | /api/categories | 카테고리 전체 조회 |
| POST | /api/categories | 카테고리 생성 |

</details>

---

<details>
<summary> Todo List API</summary>

<br>

| Method | URL | 설명 |
|---------|-----|------|
| GET | /api/todo-lists | TodoList 전체 조회 |
| POST | /api/todo-lists | TodoList 생성 |
| GET | /api/todo-lists/{listId} | TodoList 단건 조회 |
| PUT | /api/todo-lists/{listId} | TodoList 이름 수정 |
| DELETE | /api/todo-lists/{listId} | TodoList 삭제 |

</details>

---

<details>
<summary> To-Do API</summary>

<br>

| Method | URL | 설명 |
|---------|-----|------|
| GET | /api/todos | To-Do 전체 조회 |
| POST | /api/todos | To-Do 생성 |
| PUT | /api/todos/{todoId} | To-Do 수정 |
| DELETE | /api/todos/{todoId} | To-Do 삭제 |
| PATCH | /api/todos/{todoId}/completed | To-Do 완료 상태 변경 |
| PATCH | /api/todos/{todoId}/category | To-Do 카테고리 변경 |

</details>

---

# 🧪 테스트

- JUnit5
- Mockito

### 테스트 항목

- Auth
- Calendar
- To-Do

---

# 👨‍💻 팀원 소개

| 이름 | 담당 |
|------|------|
| 박종범 | 인증/인가, JWT, Refresh Token, 로그아웃 |
| 권도하 | Calendar 기능 |
| 전송흔 | To-Do 및 Front-End |
