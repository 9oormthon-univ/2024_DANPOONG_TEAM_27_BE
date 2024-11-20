package com.luckit.goal.controller.dto;

public record AddGoalDto(
        String name,
        boolean isCompleted,
        int start_date_year,
        int start_date_month,
        int start_date_day,
        int end_date_year,
        int end_date_month,
        int end_date_day
) {
}
