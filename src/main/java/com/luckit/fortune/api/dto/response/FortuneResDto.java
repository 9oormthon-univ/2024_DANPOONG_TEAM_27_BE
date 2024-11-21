package com.luckit.fortune.api.dto.response;

import com.luckit.fortune.domain.Message;
import java.util.List;

public record FortuneResDto(
        List<Choice> choices,
        Usage usage
) {
    public record Choice(
            int index, Message message
    ) {}

    public record Usage(
            int prompt_tokens,
            int completion_tokens,
            int total_tokens
    ) {}
}
