package com.luckit.todo.controller.dto;

import com.luckit.fortune.api.dto.response.UserMissionResDto;
import lombok.Builder;

@Builder
public record GetTodoDto(
        Integer todoId,
        String name,
        int year,
        int month,
        int day,
        boolean isCompleted,
        boolean isMadeByGpt,
        UserMissionResDto.FortuneType fortuneType,
        int score,
        int animal
) {
}
