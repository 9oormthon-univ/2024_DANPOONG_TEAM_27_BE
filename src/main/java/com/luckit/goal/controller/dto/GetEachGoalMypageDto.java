package com.luckit.goal.controller.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GetEachGoalMypageDto(
        int start_date_year,
        int start_date_month,
        int start_date_day,
        int end_date_year,
        int end_date_month,
        int end_date_day,
        List<Integer> countAnimals
) {
}
