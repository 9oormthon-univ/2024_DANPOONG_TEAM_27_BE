package com.luckit.user.controller;

import com.luckit.global.dto.UserInfo;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.user.controller.dto.CreateUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Tag(name = "User", description = "User API")
public interface UserControllerDocs {

    @Operation(
            summary = "로그인 유저 정보",
            description = "로그인 유저 정보 조회 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user information.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserInfo.class) // UserInfo 타입 반환
                            )
                    ),
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
                    @ApiResponse(
                            responseCode = "200",
                            description = "User fortune created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class) // String 타입 반환
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<String> createUserFortune(
            Principal principal,
            @RequestBody CreateUserDto requestDto
    );

    @Operation(
            summary = "사주 정보 조회",
            description = "사주 정보 조회 API (성별, 생년월일, 태어난 시간 등)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user fortune.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateUserDto.class) // CreateUserDto 타입 반환
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<CreateUserDto> getUserFortune(
            Principal principal
    );
}