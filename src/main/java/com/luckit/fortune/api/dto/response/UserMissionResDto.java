package com.luckit.fortune.api.dto.response;

import java.util.Map;

public record UserMissionResDto(
        String missionName,
        Map<FortuneType, Integer> scores
) {
    public enum FortuneType {
        LOVE, MONEY, CAREER, STUDY, HEALTH
    }
}

