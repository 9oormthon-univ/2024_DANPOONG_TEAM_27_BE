package com.luckit.todo.controller.dto;

import lombok.Builder;

@Builder
public record GetTodoDto(
        Integer todoId,
        String name,
        int year,
        int month,
        int day,
        boolean isCompleted,
        int animal
) {
}
