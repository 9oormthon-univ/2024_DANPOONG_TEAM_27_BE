package com.luckit.fortune.api.dto.request;

import com.luckit.fortune.domain.Message;
import java.util.List;

public record FortuneReqDto(
        String model,
        List<Message> messages
) {
}
