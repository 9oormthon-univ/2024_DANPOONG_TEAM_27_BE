package com.luckit.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {

    ROLE_USER("USER", "사용자"),
    ROLE_ADMIN("ADMIN", "관리자");

    private final String code;
    private final String displayName;
}
