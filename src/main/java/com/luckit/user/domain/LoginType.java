package com.luckit.user.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum LoginType {

    KAKAO_LOGIN("KAKAO_LOGIN", "카카오 로그인");

    private final String code;
    private final String displayName;
}
