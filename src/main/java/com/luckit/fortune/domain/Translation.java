package com.luckit.fortune.domain;

public record Translation(
        String detected_source_language,
        String text
) {
}
