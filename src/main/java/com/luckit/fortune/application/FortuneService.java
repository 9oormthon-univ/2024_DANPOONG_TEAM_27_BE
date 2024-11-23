package com.luckit.fortune.application;

import com.luckit.fortune.api.dto.request.FortuneReqDto;
import com.luckit.fortune.api.dto.response.FortuneResDto;
import com.luckit.fortune.api.dto.response.GoalPeriod;
import com.luckit.fortune.api.dto.response.UserFortuneResponseDto;
import com.luckit.fortune.domain.Fortune;
import com.luckit.fortune.domain.FortuneRepository;
import com.luckit.fortune.api.dto.response.UserMissionResDto;
import com.luckit.fortune.domain.Message;
import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.goal.service.GoalService;
import com.luckit.user.domain.User;
import com.luckit.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
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
    private final FortuneRepository fortuneRepository;
    private final GoalService goalService;

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

        String userName = user.getName();
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

        LocalDate today = LocalDate.now();
        if (fortuneRepository.existsByUserAndDate(user, today)) {

            Fortune fortune = fortuneRepository.findByUserAndDate(user, today)
                    .orElseThrow(() -> new CustomException(ErrorCode.NO_FORTUNE_ERROR, ErrorCode.NO_FORTUNE_ERROR.getMessage()));

            UserFortuneResponseDto userFortuneResponseDto =  UserFortuneResponseDto.builder()
                    .fortuneKeywords(fortune.getFortuneKeywords())
                    .shortFortune(fortune.getShortFortune())
                    .fullFortune(fortune.getFullFortune())
                    .categoryFortuneScores(fortune.getCategoryFortuneScores())
                    .timeOfDayFortuneScores(fortune.getTimeOfDayFortuneScores())
                    .overallFortuneScore(fortune.getOverallFortuneScore())
                    .build();

            return ApiResponseTemplate.success(SuccessCode.GET_FORTUNE_SUCCESS,userFortuneResponseDto);
        }

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

        Fortune fortune = Fortune.builder()
                .user(user)
                .fortuneKeywords(translatedFortuneResponse.fortuneKeywords())
                .categoryFortuneScores(translatedFortuneResponse.categoryFortuneScores())
                .timeOfDayFortuneScores(translatedFortuneResponse.timeOfDayFortuneScores())
                .overallFortuneScore(translatedFortuneResponse.overallFortuneScore())
                .shortFortune(translatedFortuneResponse.shortFortune())
                .fullFortune(translatedFortuneResponse.fullFortune())
                .build();

        fortuneRepository.save(fortune);

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

    @Transactional
    public ApiResponseTemplate<List<UserMissionResDto>> createDailyMission(Principal principal) {
        Integer userId = Integer.parseInt(principal.getName());
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER_EXCEPTION,
                        ErrorCode.NOT_FOUND_MEMBER_EXCEPTION.getMessage()));

        String prompt = generateMissionPrompt(user);
        String translatedPrompt = translationService.translate(prompt, "EN");

        FortuneReqDto reqDto = new FortuneReqDto(model, List.of(new com.luckit.fortune.domain.Message("user", translatedPrompt)));
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

        List<UserMissionResDto> missionResDto = parseMissionResponse(translatedResponseToKO);

        return ApiResponseTemplate.success(SuccessCode.GET_USER_MISSION_SUCCESS, missionResDto);
    }

    private String generateMissionPrompt(User user) {
        return String.format(
                "You need to provide a mission to increase your property based on your target '%s'. Please keep your response format strict as follows.\n" +
                        "Include the following information:\n" +
                        "1. **Mission name**: The name of the mission.\n" +
                        "2. **That type**: Please use one of the following types: LOVE, MONEY, CAREER, STUDY, HEALTH.\n" +
                        "   Provide the type followed by a score (e.g., LOVE 10 points). Each mission should only have one type with one score.\n" +
                        "Provide one or more missions in the exact format without any additional details.",
                goalService.getGoal(user.getUserId())
        );
    }

    private List<UserMissionResDto> parseMissionResponse(String response) {
        String[] lines = response.split("\\R");
        List<UserMissionResDto> missionList = new ArrayList<>();

        String missionName = null;
        Map<UserMissionResDto.FortuneType, Integer> scores = new HashMap<>();

        Map<String, UserMissionResDto.FortuneType> typeTranslationMap = new HashMap<>();
        typeTranslationMap.put("사랑", UserMissionResDto.FortuneType.LOVE);
        typeTranslationMap.put("건강", UserMissionResDto.FortuneType.HEALTH);
        typeTranslationMap.put("커리어", UserMissionResDto.FortuneType.CAREER);
        typeTranslationMap.put("경력", UserMissionResDto.FortuneType.CAREER);
        typeTranslationMap.put("공부", UserMissionResDto.FortuneType.STUDY);
        typeTranslationMap.put("LOVE", UserMissionResDto.FortuneType.LOVE);
        typeTranslationMap.put("HEALTH", UserMissionResDto.FortuneType.HEALTH);
        typeTranslationMap.put("CAREER", UserMissionResDto.FortuneType.CAREER);
        typeTranslationMap.put("STUDY", UserMissionResDto.FortuneType.STUDY);

        for (String line : lines) {
            line = line.trim();
            logger.info("Processing line: {}", line);

            if (line.contains("**미션 이름**") || line.contains("**Mission name**")) {
                String[] splitLine = line.split("\\*\\*미션 이름\\*\\*: |\\*\\*Mission name\\*\\*: ");
                if (splitLine.length > 1) {
                    missionName = splitLine[1].trim();
                    logger.info("Extracted Mission Name: {}", missionName);
                }
            }
            else if (line.contains("**유형**") || line.contains("**그 유형**") || line.contains("**Type**") || line.contains("**That type**")) {
                String[] splitLine = line.split("\\*\\*(유형|그 유형|Type|That type)\\*\\*: ");
                if (splitLine.length > 1) {
                    String[] typeAndPoints = splitLine[1].split(" ");
                    if (typeAndPoints.length >= 2) {
                        String fortuneTypeStr = typeAndPoints[0].toUpperCase().trim();

                        if (typeTranslationMap.containsKey(fortuneTypeStr)) {
                            fortuneTypeStr = typeTranslationMap.get(fortuneTypeStr).name();
                        }

                        String pointsStr = typeAndPoints[1].replaceAll("[^\\d]", "").trim();
                        if (!pointsStr.isEmpty()) {
                            Integer fortunePoints = Integer.parseInt(pointsStr);

                            try {
                                UserMissionResDto.FortuneType fortuneType = UserMissionResDto.FortuneType.valueOf(fortuneTypeStr);
                                scores.put(fortuneType, fortunePoints);
                                logger.info("Extracted Type: {}, Points: {}", fortuneType, fortunePoints);
                            } catch (IllegalArgumentException e) {
                                throw new CustomException(ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION, "Invalid fortune type in response: " + fortuneTypeStr);
                            }
                        } else {
                            logger.warn("Points value is empty or invalid for line: {}", line);
                        }
                    } else {
                        logger.warn("Type and points format is unexpected for line: {}", line);
                    }
                }

                if (missionName != null && !scores.isEmpty()) {
                    UserMissionResDto mission = new UserMissionResDto(missionName, scores);
                    missionList.add(mission);
                    logger.info("Added Mission: {}", mission);

                    missionName = null;
                    scores = new HashMap<>();
                }
            }
        }

        if (missionList.isEmpty()) {
            throw new CustomException(ErrorCode.FAILED_GET_GPT_RESPONSE_EXCEPTION, "Failed to extract mission from response.");
        }

        return missionList;
    }

}

