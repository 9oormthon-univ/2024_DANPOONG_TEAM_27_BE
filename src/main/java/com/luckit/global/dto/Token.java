package com.luckit.global.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;

@Builder
public record Token (
        @SerializedName("access_token")
        String accessToken
) {
}
