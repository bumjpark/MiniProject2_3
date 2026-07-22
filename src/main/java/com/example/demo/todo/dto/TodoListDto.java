package com.example.demo.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "TodoList 응답")
public class TodoListDto {

    @Schema(description = "TodoList ID", example = "1")
    private Long listId;
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "목록 이름", example = "업무")
    private String listName;

    public TodoListDto() {
    }

    public TodoListDto(Long listId, String listName) {
        this.listId = listId;
        this.listName = listName;
    }

    public TodoListDto(Long listId, Long userId, String listName) {
        this.listId = listId;
        this.userId = userId;
        this.listName = listName;
    }

	public Long getListId() {
		return listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}
}
