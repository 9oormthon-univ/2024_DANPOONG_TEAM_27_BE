package com.luckit.fortune.api.dto.response;

import com.luckit.fortune.domain.Translation;
import java.util.List;

public record TranslationResDto(
        List<Translation> translations
) {
}
