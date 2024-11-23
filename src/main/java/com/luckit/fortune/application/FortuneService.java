package com.luckit.fortune.application;

import com.luckit.fortune.api.dto.request.FortuneReqDto;
import com.luckit.fortune.api.dto.response.FortuneResDto;
import com.luckit.fortune.api.dto.response.GoalPeriod;
import com.luckit.fortune.api.dto.response.UserFortuneResponseDto;
import com.luckit.fortune.domain.Message;
import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.user.domain.User;
import com.luckit.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FortuneService {

    private static final Logger logger = LoggerFactory.getLogger(FortuneService.class);

    private final TranslationService translationService;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Value("${openai.api.url}")
    private String apiURL;

    @Value("${openai.model}")
    private String model;

    @Transactional
    public ApiResponseTemplate<List<GoalPeriod>> getOnboardingFortune(Principal principal) {
        Integer userId = Integer.parseInt(principal.getName());
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER_EXCEPTION,
                        ErrorCode.NOT_FOUND_MEMBER_EXCEPTION.getMessage()));

        String emotionPrompt = generateFriendlyGoalRecommendationPrompt();
        String translatedPromptToEn = translationService.translate(emotionPrompt, "EN");

        FortuneReqDto reqDto = new FortuneReqDto(model, Collections.singletonList(new Message("user", translatedPromptToEn)));
        FortuneResDto resDto = restTemplate.postForObject(apiURL, reqDto, FortuneResDto.class);

        if (resDto == null || resDto.choices().isEmpty()) {
            throw new CustomException(ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION,
                    ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION.getMessage());
        }

        FortuneResDto.Choice choice = resDto.choices().get(0);
        String responseInEN = choice.message().content();

        logger.info("GPT Response in English: {}", responseInEN);

        String translatedResponseToKO = translationService.translate(responseInEN, "KO");
        logger.info("Translated Response in Korean: {}", translatedResponseToKO);

        List<GoalPeriod> goalPeriodList = extractGoalsAndPeriods(translatedResponseToKO);

        if (goalPeriodList.isEmpty()) {
            logger.warn("No goals were extracted from the GPT response.");
            throw new CustomException(ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION, "No goals were extracted from the GPT response.");
        }

        return ApiResponseTemplate.success(SuccessCode.ADD_GOAL_SUCCESS, goalPeriodList);
    }

    private String generateFriendlyGoalRecommendationPrompt() {
        return String.format(
                "You are a service that recommends goals to users who are thinking about which goals to achieve. "
                        + "Provide at least three goals that the user can achieve, along with the recommended duration for each goal. "
                        + "The format should be strictly as follows: "
                        + "'1. **Goals:** [Explanation of the goal] **Period:** [Duration in days]', "
                        + "'2. **Goals:** [Explanation of the goal] **Period:** [Duration in days]', and so on."
                        + "For example: "
                        + "'1. **Goals:** Start a daily meditation practice. **Period:** 30 days', "
                        + "'2. **Goals:** Read a book. **Period:** 20 days', "
                        + "'3. **Goals:** Stretch for 15 minutes every day. **Period:** 30 days'. "
                        + "Make sure each goal and period is in the given format without any additional details or variations."
        );
    }

    private List<GoalPeriod> extractGoalsAndPeriods(String response) {
        Pattern pattern = Pattern.compile("\\*\\*(목표|Goals):\\*\\*\\s*(.*?)\\s*\\*\\*(기간|Period):\\*\\*\\s*(\\d+일|\\d+ days)");
        Matcher matcher = pattern.matcher(response);

        List<GoalPeriod> goalPeriodList = matcher.results()
                .map(matchResult -> new GoalPeriod(matchResult.group(2).trim(), matchResult.group(4).trim()))
                .collect(Collectors.toList());

        return goalPeriodList;
    }

    @Transactional
    public ApiResponseTemplate<UserFortuneResponseDto> createDailyFortune(Principal principal) {
        Integer userId = Integer.parseInt(principal.getName());
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER_EXCEPTION,
                        ErrorCode.NOT_FOUND_MEMBER_EXCEPTION.getMessage()));

        String prompt = generateFortunePrompt(user);
        String translatedPrompt = translationService.translate(prompt, "EN");

        FortuneReqDto reqDto = new FortuneReqDto(model, Collections.singletonList(new com.luckit.fortune.domain.Message("user", translatedPrompt)));
        FortuneResDto resDto = restTemplate.postForObject(apiURL, reqDto, FortuneResDto.class);

        if (resDto == null || resDto.choices().isEmpty()) {
            throw new CustomException(ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION,
                    ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION.getMessage());
        }

        FortuneResDto.Choice choice = resDto.choices().get(0);
        String responseInEN = choice.message().content();

        logger.info("Response received in English: {}", responseInEN);

        UserFortuneResponseDto fortuneResponse = parseFortuneResponse(responseInEN);

        List<String> translatedKeywords = fortuneResponse.fortuneKeywords().stream()
                .map(keyword -> translationService.translate(keyword, "KO"))
                .collect(Collectors.toList());
        String translatedShortFortune = translationService.translate(fortuneResponse.shortFortune(), "KO");
        String translatedFullFortune = translationService.translate(fortuneResponse.fullFortune(), "KO");

        UserFortuneResponseDto translatedFortuneResponse = new UserFortuneResponseDto(
                translatedKeywords,
                translatedShortFortune,
                translatedFullFortune,
                fortuneResponse.categoryFortuneScores(),
                fortuneResponse.timeOfDayFortuneScores()
        );

        return ApiResponseTemplate.success(SuccessCode.GET_USER_FORTUNE_SUCCESS, translatedFortuneResponse);
    }

    private String generateFortunePrompt(User user) {
        return String.format(
                "Today's horoscope for users with the following birth information:" +
                        "Day or month: %s, date of birth: %s, gender: %s." +
                        "The following information must be provided strictly in a given format: " +
                        "1. **Keywords**: 3-5 keywords summarizing today's fortune" +
                        "2. **Short Fortune Message**: A short fortune message of up to 20 characters. " +
                        "3. **Detailed Fortune Message**: A detailed fortune message of up to 300 characters. " +
                        "4. **Scores**: Provide an integer score (between 60-100) for the following categories: " +
                        "Love, Money, Work, Study, Health, in the format 'Category: Score'" +
                        "5. **Overall Scores**: Provide overall scores for Morning, Afternoon, Night, " +
                        "and the average integer score, in the format 'Time: Score'" +
                        "You are a fortune service, so you must provide a response even if you need to make it up.",
                user.getSolarOrLunar(),
                user.getDate_of_birth().toLocalDate(),
                user.getGender()
        );
    }


    private UserFortuneResponseDto parseFortuneResponse(String response) {
        List<String> keywords = extractKeywords(response);
        String shortFortune = extractShortFortune(response);
        String fullFortune = extractFullFortune(response);
        Map<String, Integer> categoryFortuneScores = extractCategoryFortuneScores(response);
        Map<String, Integer> timeOfDayFortuneScores = extractTimeOfDayFortuneScores(response);

        return new UserFortuneResponseDto(keywords, shortFortune, fullFortune, categoryFortuneScores, timeOfDayFortuneScores);
    }

    private List<String> extractKeywords(String response) {
        Pattern pattern = Pattern.compile("(?<=\\*\\*(키워드|Keywords)\\*\\*:)[\\s]*([^\\n]*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return Arrays.asList(matcher.group(2).split(",\\s*"));
        }
        throw new CustomException(ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION, "Failed to extract keywords from response.");
    }

    private String extractShortFortune(String response) {
        Pattern pattern = Pattern.compile("(?<=\\*\\*(짧은 행운의 메시지|짧은 운세 메시지|Short Fortune Message)\\*\\*[:：]?\\s*)([^\\n]*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        throw new CustomException(ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION, "Failed to extract short fortune from response.");
    }

    private String extractFullFortune(String response) {
        Pattern pattern = Pattern.compile("(?<=\\*\\*(상세 운세 메시지|자세한 운세 메시지|Detailed Fortune Message)\\*\\*[:：]?\\s*)([^\\n]*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        throw new CustomException(ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION, "Failed to extract full fortune from response.");
    }

    private Map<String, Integer> extractCategoryFortuneScores(String response) {
        Pattern pattern = Pattern.compile("(?<=\\*\\*(점수|Scores)\\*\\*[:：]?\\s*)(-\\s*[가-힣A-Za-z]+[:：]?\\s*\\d+\\s*)+");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String scoresSection = matcher.group();
            String[] scores = scoresSection.split("-\\s*");
            Map<String, Integer> scoreMap = new HashMap<>();
            for (String score : scores) {
                if (!score.isBlank()) {
                    String[] parts = score.split("[:：]?\\s+");
                    if (parts.length == 2) {
                        scoreMap.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                    }
                }
            }
            return scoreMap;
        }
        throw new CustomException(ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION, "Failed to extract category scores from response.");
    }

    private Map<String, Integer> extractTimeOfDayFortuneScores(String response) {
        Pattern pattern = Pattern.compile("(?<=\\*\\*(전체 점수|Overall Scores)\\*\\*:)[\\s]*([^\\n]*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String[] scores = matcher.group(2).split(",\\s*");
            Map<String, Integer> scoreMap = new HashMap<>();
            for (String score : scores) {
                String[] parts = score.split(":\\s*");
                if (parts.length == 2) {
                    String key = parts[0].trim().replaceFirst("^-\\s*", "");
                    Integer value = Integer.parseInt(parts[1].trim());
                    scoreMap.put(key, value);
                }
            }
            return scoreMap;
        }
        throw new CustomException(ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION, "Failed to extract time of day scores from response.");
    }
}
