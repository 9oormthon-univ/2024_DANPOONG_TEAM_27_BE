package com.luckit.goal.controller;


import com.luckit.global.dto.UserInfo;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.goal.controller.dto.AddGoalDto;
import com.luckit.goal.controller.dto.GetGoalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}
