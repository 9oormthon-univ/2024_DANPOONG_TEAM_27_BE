package com.luckit.goal.controller.dto;

import lombok.Builder;

@Builder
public record GetGoalMypageDto(
        Integer goalId,
        String name,
        int countSuccessTodo
) {
}
