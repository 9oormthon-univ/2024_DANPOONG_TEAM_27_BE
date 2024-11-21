package com.luckit.gpt.api;

import com.luckit.gpt.api.dto.request.TranslationReqDto;
import com.luckit.gpt.api.dto.response.TranslationResDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class TranslationController {

    @Value("${deepl.api.url}")
    private String deeplApiUrl;

    @Value("${deepl.api.key}")
    private String authKey;

    @PostMapping("/translate")
    public ResponseEntity<TranslationResDto> translate(@RequestBody TranslationReqDto request) {
        String targetLang = request.target_lang();

        String apiUrl = deeplApiUrl + "?target_lang=" + targetLang;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "DeepL-Auth-Key " + authKey);

        TranslationResDto response = restTemplate.postForObject(apiUrl, createHttpEntity(request, headers), TranslationResDto.class);

        return ResponseEntity.ok(response);
    }

    private HttpEntity<TranslationReqDto> createHttpEntity(TranslationReqDto request, HttpHeaders headers) {
        return new HttpEntity<>(request, headers);
    }
}