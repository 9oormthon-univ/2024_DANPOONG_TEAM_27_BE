package com.luckit.todo.controller.dto;

import lombok.Builder;

@Builder
public record GetTodoDto(
        Integer todoId,
        String name,
        boolean isCompleted
) {
}
