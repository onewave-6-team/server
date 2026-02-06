package org.example.explog.controller;

import lombok.RequiredArgsConstructor;
import org.example.explog.service.GeminiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GeminiTestController {

    private final GeminiService geminiService;

    // 브라우저에서 바로 테스트해볼 수 있는 GET 방식 API
    // 주소: http://localhost:8080/api/test/gemini?text=하고싶은말
    @GetMapping("/api/test/gemini")
    public GeminiService.GeminiResponse test(@RequestParam String text) {
        return geminiService.getAnalysis(text);
    }
}