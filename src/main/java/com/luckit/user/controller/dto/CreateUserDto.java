package com.luckit.user.controller.dto;

import lombok.Builder;

@Builder
public record CreateUserDto(
        String gender,
        String solarOrLunar,
        int year,
        int month,
        int day,
        int hour,
        int minute,
        boolean unknownTime
) {

}
