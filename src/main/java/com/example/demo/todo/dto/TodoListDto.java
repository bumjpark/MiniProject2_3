package com.example.demo.todo.dto;

public class TodoListDto {

    private Long listId;
    private String listName;

    public TodoListDto() {
    }

    public TodoListDto(Long listId, String listName) {
        this.listId = listId;
        this.listName = listName;
    }

	public Long getListId() {
		return listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}
}