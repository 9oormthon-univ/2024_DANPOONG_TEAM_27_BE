package com.luckit.user.controller;

import com.luckit.global.dto.UserInfo;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.user.controller.dto.CreateUserRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

public interface UserControllerDocs {

    @Operation(
            summary = "로그인 유저 정보",
            description = "로그인 유저 정보 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseTemplate.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<UserInfo> getUserInfo(
            Principal principal
    );

    @Operation(
            summary = "사주 정보 등록",
            description = "사주 정보 등록 API (성별, 생년월일, 태어난 시간 등)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseTemplate.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<String> createUser(
            Principal principal,
            @RequestBody CreateUserRequestDto requestDto
    );
}