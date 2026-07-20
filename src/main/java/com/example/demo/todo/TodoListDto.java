package com.example.demo.todo;

public class TodoListDto {

    private Long listId;
    private String name;

    public TodoListDto() {
    }

    public TodoListDto(Long listId, String name) {
        this.listId = listId;
        this.name = name;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}