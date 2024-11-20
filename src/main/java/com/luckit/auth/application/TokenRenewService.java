package com.luckit.auth.application;


import com.luckit.auth.api.dto.response.AuthResDto;
import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.jwt.TokenProvider;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.user.domain.User;
import com.luckit.user.domain.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenRenewService {

    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public ApiResponseTemplate<AuthResDto> renewAccessToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;

        if (isBlacklisted(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN_EXCEPTION,
                    ErrorCode.INVALID_TOKEN_EXCEPTION.getMessage());
        }

        String userIdStr = redisTemplate.opsForValue().get(key);

        if (userIdStr == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN_EXCEPTION,
                    ErrorCode.INVALID_TOKEN_EXCEPTION.getMessage());
        }

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN_EXCEPTION,
                    ErrorCode.INVALID_TOKEN_EXCEPTION.getMessage());
        }

        Integer userId = Integer.parseInt(userIdStr);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ID_EXCEPTION,
                        ErrorCode.NOT_FOUND_ID_EXCEPTION.getMessage()));

        String renewAccessToken = tokenProvider.createAccessToken(user);

        return ApiResponseTemplate.success(SuccessCode.RENEW_TOKEN_SUCCESS, AuthResDto.of(renewAccessToken, refreshToken));
    }

    public void saveRefreshToken(String refreshToken, Integer userId) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, userId.toString());
    }

    public void deleteRefreshToken(String refreshToken) {
        String redisKey = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.delete(redisKey);
    }

    public void addToBlacklist(String token) {
        if (token != null) {
            long expiration = getRemainingExpirationTime(token);
            if (expiration > 0) {
                redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "blacklisted", Duration.ofMillis(expiration));
            }
        }
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    private long getRemainingExpirationTime(String token) {
        Claims claims = tokenProvider.getClaimsFromToken(token);
        Date expirationDate = claims.getExpiration();
        long now = new Date().getTime();
        return expirationDate.getTime() - now;
    }
}

