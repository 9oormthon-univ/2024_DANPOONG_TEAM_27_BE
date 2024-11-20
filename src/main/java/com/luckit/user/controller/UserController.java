package com.luckit.user.controller;

import com.luckit.global.dto.UserInfo;
import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.user.controller.dto.CreateUserRequestDto;
import com.luckit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController implements UserControllerDocs{

    private final UserService userService;

    @GetMapping
    public ApiResponseTemplate<UserInfo> getUserInfo(
            Principal principal
    ) {
        return ApiResponseTemplate.success(SuccessCode.GET_MEMBER_INFO_SUCCESS, userService.getUserInfo(Integer.parseInt(principal.getName())));
    }

    @PostMapping
    public ApiResponseTemplate<String> createUser(
            Principal principal,
            @RequestBody CreateUserRequestDto requestDto
    ) {
        return ApiResponseTemplate.success(SuccessCode.USER_CREATE_SUCCESS, userService.createUser(Integer.parseInt(principal.getName()), requestDto));
    }


}
