package com.luckit.auth.application;


import com.luckit.auth.api.dto.response.AuthResDto;
import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.jwt.TokenProvider;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.user.domain.LoginType;
import com.luckit.user.domain.RoleType;
import com.luckit.user.domain.UserRepository;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.luckit.global.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import com.luckit.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KakaoOAuthService {

    @Value("${oauth.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${oauth.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${oauth.kakao.token-url}")
    private String KAKAO_TOKEN_URL;

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final TokenRenewService tokenRenewService;

    public ApiResponseTemplate<String> getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(KAKAO_TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", KAKAO_CLIENT_ID)
                .queryParam("redirect_uri", KAKAO_REDIRECT_URI)
                .queryParam("code", code)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> resEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            log.debug("Token Response: {}", resEntity.getBody());

            if (resEntity.getStatusCode().is2xxSuccessful()) {
                String json = resEntity.getBody();
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                String accessToken = jsonObject.get("access_token").getAsString(); // 액세스 토큰 직접 파싱
                return ApiResponseTemplate.success(SuccessCode.GET_TOKEN_SUCCESS, accessToken);
            }
        } catch (HttpClientErrorException e) {
            log.error("Error occurred while getting Kakao Access Token: {}", e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.FAILED_GET_TOKEN_EXCEPTION, e.getMessage());
        }

        throw new CustomException(ErrorCode.FAILED_GET_TOKEN_EXCEPTION,
                ErrorCode.FAILED_GET_TOKEN_EXCEPTION.getMessage());
    }

    @Transactional
    public ApiResponseTemplate<AuthResDto> signUpOrLogin(String kakaoAccessToken) {
        UserInfo userInfo = getUserInfo(kakaoAccessToken);

        User user = userRepository.findByEmail(userInfo.email())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(userInfo.email())
                        .name(userInfo.name())
                        .profileImage(userInfo.profileImageUrl())
                        .loginType(LoginType.KAKAO_LOGIN)
                        .roleType(RoleType.ROLE_USER)
                        .build())
                );
        String message = user.getUserId() != null && userRepository.findByEmail(userInfo.email()).isPresent()
                ? "기존에 있는 유저입니다."
                : SuccessCode.LOGIN_MEMBER_SUCCESS.getMessage();

        String accessToken = tokenProvider.createAccessToken(user);
        String refreshToken = tokenProvider.createRefreshToken(user);

        tokenRenewService.saveRefreshToken(refreshToken, user.getUserId());

        return ApiResponseTemplate.<AuthResDto>builder()
                .status(SuccessCode.LOGIN_MEMBER_SUCCESS.getHttpStatus().value())
                .message(message)
                .data(AuthResDto.of(accessToken, refreshToken))
                .build();
    }

    public UserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kapi.kakao.com/v2/user/me?access_token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

            JsonObject kakaoAccount = jsonObject.getAsJsonObject("kakao_account");
            String email = kakaoAccount.get("email").getAsString();
            JsonObject profile = kakaoAccount.getAsJsonObject("profile");
            String name = kakaoAccount.get("profile").getAsJsonObject().get("nickname").getAsString();
            String profileImageUrl = profile.has("profile_image_url") ? profile.get("profile_image_url").getAsString() : null;

            return new UserInfo(email, name,profileImageUrl);
        }

        throw new CustomException(ErrorCode.NOT_FOUND_MEMBER_EXCEPTION,
                ErrorCode.NOT_FOUND_MEMBER_EXCEPTION.getMessage());
    }
}
