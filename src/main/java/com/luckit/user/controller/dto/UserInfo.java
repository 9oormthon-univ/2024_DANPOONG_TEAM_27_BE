package com.luckit.user.controller.dto;


public record UserInfo (
        String email,
        String name,
        String profileImageUrl
) {
}
