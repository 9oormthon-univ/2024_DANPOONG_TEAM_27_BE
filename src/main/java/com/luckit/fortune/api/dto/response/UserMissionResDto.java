package com.luckit.fortune.api.dto.response;

public record UserMissionResDto(
        String missionName,
        FortuneType fortuneType
) {
    public enum FortuneType {
        LOVE, MONEY, CAREER, STUDY, HEALTH
    }
}

