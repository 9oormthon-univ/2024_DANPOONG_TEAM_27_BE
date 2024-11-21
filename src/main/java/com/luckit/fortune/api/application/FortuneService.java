package com.luckit.fortune.api.application;

import com.luckit.fortune.api.dto.request.FortuneReqDto;
import com.luckit.fortune.api.dto.response.FortuneResDto;
import com.luckit.fortune.api.dto.response.GoalPeriod;
import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.user.domain.User;
import com.luckit.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
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

        String userName = user.getName();
        String emotionPrompt = generateFriendlyGoalRecommendationPrompt();
        String translatedPromptToEn = translationService.translate(emotionPrompt, "EN");

        FortuneReqDto reqDto = new FortuneReqDto(model, Collections.singletonList(new com.luckit.fortune.domain.Message("user", translatedPromptToEn)));
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


}
