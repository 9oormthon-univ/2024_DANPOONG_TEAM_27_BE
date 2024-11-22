package com.luckit.fortune.api.dto.response;

import java.util.List;
import java.util.Map;

public record UserFortuneResponseDto(
        List<String> fortuneKeywords,
        String shortFortune,
        String fullFortune,
        Map<String, Integer> categoryFortuneScores,
        Map<String, Integer> timeOfDayFortuneScores,
        Integer overallFortuneScore
) {
    public UserFortuneResponseDto(List<String> fortuneKeywords, String shortFortune, String fullFortune,
                                  Map<String, Integer> categoryFortuneScores, Map<String, Integer> timeOfDayFortuneScores) {
        this(fortuneKeywords, shortFortune, fullFortune, categoryFortuneScores, timeOfDayFortuneScores,
                calculateOverallFortuneScore(timeOfDayFortuneScores));
    }

    private static Integer calculateOverallFortuneScore(Map<String, Integer> timeOfDayFortuneScores) {
        if (timeOfDayFortuneScores == null || timeOfDayFortuneScores.isEmpty()) {
            return 0;
        }
        return timeOfDayFortuneScores.values().stream()
                .mapToInt(Integer::intValue)
                .sum() / timeOfDayFortuneScores.size();
    }

}
