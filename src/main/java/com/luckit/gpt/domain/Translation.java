package com.luckit.gpt.domain;

public record Translation(
        String detected_source_language,
        String text
) {
}
