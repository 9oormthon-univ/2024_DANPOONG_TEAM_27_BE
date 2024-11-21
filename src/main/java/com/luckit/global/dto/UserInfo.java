package com.luckit.global.dto;

import lombok.Builder;

@Builder
public record UserInfo (
        String email,
        String name,
        String profileImageUrl
) {
}
