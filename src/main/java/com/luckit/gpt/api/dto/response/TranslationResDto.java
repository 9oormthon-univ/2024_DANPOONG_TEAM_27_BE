package com.luckit.gpt.api.dto.response;

import com.luckit.gpt.domain.Translation;
import java.util.List;

public record TranslationResDto(
        List<Translation> translations
) {
}
