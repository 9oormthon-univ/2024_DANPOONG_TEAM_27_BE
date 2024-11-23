package com.luckit.todo.controller.dto;

public record UpdateTodoDto(
        Integer todoId,
        String name
) {
}
