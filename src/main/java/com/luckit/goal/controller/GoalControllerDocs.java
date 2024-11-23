package com.luckit.goal.controller;


import com.luckit.global.dto.UserInfo;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.goal.controller.dto.AddGoalDto;
import com.luckit.goal.controller.dto.CompleteGoalDto;
import com.luckit.goal.controller.dto.GetGoalDto;
import com.luckit.goal.controller.dto.GetGoalMypageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.List;

@Tag(name = "Goal", description = "Goal API")
public interface GoalControllerDocs {

    @Operation(
            summary = "목표 생성",
            description = "목표 생성 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Goal successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<String> addGoal(
            Principal principal,
            @RequestBody AddGoalDto addGoalDto
    );


    @Operation(
            summary = "목표 조회",
            description = "목표 조회 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved goal information.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema( // 배열 타입 명시
                                            schema = @Schema(implementation = GetGoalDto.class) // 개별 요소의 스키마 정의
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<List<GetGoalDto>> getGoal(
            Principal principal
    );

    @Operation(
            summary = "목표 목록 조회 (마이페이지)",
            description = "목표 목록 조회 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved goal information.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema( // 배열 타입 명시
                                            schema = @Schema(implementation = GetGoalMypageDto.class) // 개별 요소의 스키마 정의
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<List<GetGoalMypageDto>> getGoalMypage(
            Principal principal
    );

    @Operation(
            summary = "목표 개별 조회 (마이페이지)",
            description = "목표 개별 조회 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved goal information.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema( // 배열 타입 명시
                                            schema = @Schema(implementation = Integer.class)
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<List<Integer>> getEachGoalMypage(
            @PathVariable("goal_id") Integer goalId
    );

    @Operation(
            summary = "목표 완료 상태 변경",
            description = "목표 상태 변경 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully change goal status.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CompleteGoalDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<CompleteGoalDto> completeGoal(
            @PathVariable("goal_id") Integer goalId
    );

    @Operation(
            summary = "목표 삭제",
            description = "목표 삭제 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully delete goal.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<String> deleteGoal(
            @PathVariable("goal_id") Integer goalId
    );
}
