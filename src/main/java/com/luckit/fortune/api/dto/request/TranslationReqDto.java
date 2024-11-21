package com.luckit.fortune.api.dto.request;

import java.util.List;

public record TranslationReqDto(
        List<String> text,
        String target_lang
) {
}