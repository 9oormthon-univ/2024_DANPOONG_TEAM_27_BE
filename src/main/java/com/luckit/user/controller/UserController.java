package com.luckit.user.controller;

import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.user.controller.dto.CreateUserRequestDto;
import com.luckit.user.controller.dto.UserInfo;
import com.luckit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController implements UserControllerDocs{

    private final UserService userService;

    @GetMapping
    public ApiResponseTemplate<UserInfo> getUserById(
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ApiResponseTemplate.success(SuccessCode.GET_MEMBER_INFO_SUCCESS, userInfo);
    }

    @PostMapping
    public ApiResponseTemplate<UserInfo> createUser(
            @AuthenticationPrincipal UserInfo userInfo,
            @RequestBody CreateUserRequestDto requestDto
    ) {
        return ApiResponseTemplate.success(SuccessCode.USER_CREATE_SUCCESS, userService.createUser(userInfo.email(), requestDto));
    }


}
