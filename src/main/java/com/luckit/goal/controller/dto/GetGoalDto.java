package com.luckit.goal.controller.dto;

import lombok.Builder;

@Builder
public record GetGoalDto(
        Integer goalId,
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
