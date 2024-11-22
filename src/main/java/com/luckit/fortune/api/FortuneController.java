package com.luckit.fortune.api;

import com.luckit.fortune.api.application.FortuneService;
import com.luckit.fortune.api.dto.response.GoalPeriod;
import com.luckit.global.template.ApiResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping
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
}
