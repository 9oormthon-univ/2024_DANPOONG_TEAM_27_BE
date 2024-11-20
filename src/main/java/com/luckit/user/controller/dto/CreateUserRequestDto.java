package com.luckit.user.controller.dto;

public record CreateUserRequestDto(
        String gender,
        String solarOrLunar,
        int year,
        int month,
        int day,
        int hour,
        int minute
) {

}
