package com.luckit.todo.controller.dto;

public record AddTodoDto(
        Integer goalId,
        String name,
        int year,
        int month,
        int day
) {
}
