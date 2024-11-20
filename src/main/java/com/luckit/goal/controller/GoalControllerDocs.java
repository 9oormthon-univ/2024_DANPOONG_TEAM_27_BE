package com.luckit.goal.controller;


import com.luckit.global.dto.UserInfo;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.goal.controller.dto.AddGoalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

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
}
