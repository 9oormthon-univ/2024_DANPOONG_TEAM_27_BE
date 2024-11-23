package com.luckit.auth.api;

import com.luckit.auth.api.dto.request.RefreshTokenReqDto;
import com.luckit.auth.api.dto.response.AuthResDto;
import com.luckit.auth.application.KakaoOAuthService;
import com.luckit.auth.application.TokenRenewService;
import com.luckit.global.template.ApiResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Tag(name = "회원가입/로그인", description = "회원가입/로그인을 담당하는 api 그룹")
@RequestMapping("/api/v1")
public class AuthController {

    private final KakaoOAuthService kakaoOAuthService;
    private final TokenRenewService tokenRenewService;


    @GetMapping("/callback")
    @Operation(
            summary = "카카오 회원가입/로그인 콜백",
            description = "카카오 로그인 후 리다이렉션된 URI입니다. 인가 코드를 받아서 accessToken을 요청하고, 회원가입 또는 로그인을 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입/로그인 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public ResponseEntity<ApiResponseTemplate<AuthResDto>> kakaoCallback(
            @RequestParam(name = "code") String code) {

        ApiResponseTemplate<AuthResDto> data = kakaoOAuthService.signUpOrLogin(kakaoOAuthService.getKakaoAccessToken(code).getData());
        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @PostMapping("/renew")
    @Operation(
            summary = "accessToken 재발급",
            description = "refreshToken을 사용하여 새로운 accessToken을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public ResponseEntity<ApiResponseTemplate<AuthResDto>> renewAccessToken(
            @RequestBody RefreshTokenReqDto reqDto) {

        ApiResponseTemplate<AuthResDto> data = tokenRenewService.renewAccessToken(reqDto.refreshToken());
        return ResponseEntity.status(data.getStatus()).body(data);
    }
}

