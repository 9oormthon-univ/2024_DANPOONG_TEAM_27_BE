package com.luckit.fortune.api;

import com.luckit.fortune.application.FortuneService;
import com.luckit.fortune.api.dto.response.GoalPeriod;
import com.luckit.fortune.api.dto.response.UserFortuneResponseDto;
import com.luckit.global.template.ApiResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/api/v1/fortune")
@Tag(name = "운세 생성", description = "운세 생성을 담당하는 API 그룹")
public class FortuneController {

    private final FortuneService fortuneService;

    @GetMapping("/onboarding")
    @Operation(
            summary = "유저의 온보딩 목표를 생성",
            description = "회원가입한 유저의 온보딩 목표를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "온보딩 목표 생성을 성공했습니다.")
            }
    )
    public ResponseEntity<ApiResponseTemplate<List<GoalPeriod>>> getOnboardingFortune(Principal principal) {

        ApiResponseTemplate<List<GoalPeriod>> data = fortuneService.getOnboardingFortune(principal);

        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @GetMapping("/daily")
    @Operation(
            summary = "유저의 일일 운세를 생성",
            description = "특정 유저의 일일 운세를 생성합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "일일 운세 생성을 성공했습니다.",
                            content = @Content(
                                    schema = @Schema(
                                            example = """
                                                    {
                                                        "status": 200,
                                                        "message": "사주 정보 조회에 성공했습니다",
                                                        "data": {
                                                            "fortuneKeywords": ["새로운 시작", "에너지", "기회", "초점"],
                                                            "shortFortune": "새로운 길을 받아들이세요.",
                                                            "fullFortune": "오늘은 새로운 시작입니다. 열정을 가지고 새로운 기회를 맞이하세요. 최적의 생산성을 위해 에너지의 균형을 맞추고 당면한 업무에 집중하세요. 자신의 능력을 믿고 긍정적인 시각을 유지하세요.",
                                                            "categoryFortuneScores": {
                                                                "Money": 80,
                                                                "Study": 78,
                                                                "Health": 82,
                                                                "Love": 75,
                                                                "Work": 85
                                                            },
                                                            "timeOfDayFortuneScores": {
                                                                "Morning": 80,
                                                                "Afternoon": 83,
                                                                "Night": 81,
                                                                "Average": 81
                                                            },
                                                            "overallFortuneScore": 81
                                                        }
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponseTemplate<UserFortuneResponseDto>> createDailyFortune(Principal principal) {

        ApiResponseTemplate<UserFortuneResponseDto> data = fortuneService.createDailyFortune(principal);

        return ResponseEntity.status(data.getStatus()).body(data);
    }
}
