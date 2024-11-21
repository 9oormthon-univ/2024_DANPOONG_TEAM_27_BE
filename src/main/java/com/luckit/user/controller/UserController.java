package com.luckit.user.controller;

import com.luckit.global.dto.UserInfo;
import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.user.controller.dto.CreateUserDto;
import com.luckit.user.service.UserService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/fortune")
    public ApiResponseTemplate<String> createUserFortune(
            Principal principal,
            @RequestBody CreateUserDto requestDto
    ) {
        return ApiResponseTemplate.success(SuccessCode.USER_CREATE_SUCCESS, userService.createUserFortune(Integer.parseInt(principal.getName()), requestDto));
    }

    @GetMapping("/fortune")
    public ApiResponseTemplate<CreateUserDto> getUserFortune(
            Principal principal
    ) {
        return ApiResponseTemplate.success(SuccessCode.GET_USER_FORTUNE_SUCCESS, userService.getUserFortune(Integer.parseInt(principal.getName())));
    }



}
