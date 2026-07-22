package com.example.demo.todo.dto;

public class TodoListDto {

    private Long listId;
    private Long userId;
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
