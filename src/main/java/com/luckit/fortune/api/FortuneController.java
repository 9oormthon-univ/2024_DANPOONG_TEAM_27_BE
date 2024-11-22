package com.luckit.fortune.api;

import com.luckit.fortune.api.application.FortuneService;
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
@Tag(name = "운세 생성", description = "운세 생성을 담당하는 api 그룹")
public class FortuneController {

    private final FortuneService fortuneService;

    @GetMapping("/onboarding")
    @Operation(
            summary = "유저의 온보딩 목표를 생성",
            description = "회원가입한 유저의 온보딩 목표를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "목표 생성을 성공했습니다.")
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
                            description = "운세 생성을 성공했습니다.",
                            content = @Content(schema = @Schema(example = "{ \"status\": 200, \"message\": \"사주 정보 조회에 성공했습니다\", \"data\": { \"fortuneKeywords\": [\"새로운 시작\", \"에너지\", \"호기심\", \"성장\"], \"shortFortune\": \"새로운 시작을 받아들이세요.\", \"fullFortune\": \"오늘은 새로운 기회와 경험을 받아들이기에 완벽한 날입니다. 호기심과 낙관적인 태도로 도전에 임할 수 있는 에너지가 넘치는 날입니다. 열린 마음을 유지하면 예상치 못한 영역에서 성장을 발견할 수 있습니다.\", \"categoryFortuneScores\": { \"Money\": 75, \"Study\": 90, \"Health\": 85, \"Love\": 82, \"Work\": 88 }, \"timeOfDayFortuneScores\": { \"Morning\": 83 }, \"overallFortuneScore\": 83 } }"))
                    )
            }
    )
public ResponseEntity<ApiResponseTemplate<UserFortuneResponseDto>> createDailyFortune(Principal principal) {

    ApiResponseTemplate<UserFortuneResponseDto> data = fortuneService.createDailyFortune(principal);

    return ResponseEntity.status(data.getStatus()).body(data);
}
}
