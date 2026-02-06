package org.example.explog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.url}")
    private String apiUrl;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper; // JSON 파싱용 (Spring이 자동 주입)

    public GeminiResponse getAnalysis(String userInput) {
        // URL 뒤에 API 키 붙이기
        String url = apiUrl + apiKey;

        // 1. 프롬프트 구성: "JSON만 뱉어라"고 강력하게 지시
        String prompt = """
                사용자의 경험 내용을 분석해서 다음 JSON 형식으로 답변해줘.
                마크다운 코드 블럭(```json) 없이 순수한 JSON 문자열만 반환해.
                
                {
                    "title": "20자 이내의 매력적인 제목 (핵심 키워드 포함)",
                    "summary": "한 줄 요약 (존댓말, ~했습니다 체)"
                }
                
                [사용자 입력]: %s
                """.formatted(userInput);

        // 2. 요청 바디 만들기 (Gemini API 스펙 준수)
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            // 3. API 호출
            GeminiApiResponse apiResponse = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GeminiApiResponse.class);

            // 4. 응답 추출 및 파싱
            if (apiResponse != null && !apiResponse.candidates().isEmpty()) {
                String text = apiResponse.candidates().get(0).content().parts().get(0).text();

                // 혹시 모를 마크다운 코드 블럭 제거 (```json ... ```)
                String cleanJson = text.replace("```json", "")
                                       .replace("```", "")
                                       .trim();

                return objectMapper.readValue(cleanJson, GeminiResponse.class);
            }

        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생: {}", e.getMessage());
            // 에러 발생 시 로그 찍고 기본값 반환 (서버 안 죽게 방어)
            return new GeminiResponse("제목 생성 실패", "내용을 요약하지 못했습니다.");
        }

        return new GeminiResponse("분석 불가", "API 응답이 비어있습니다.");
    }

    // --- DTO 클래스들 (Record 사용) ---

    // 1. 우리가 서비스에서 쓸 최종 결과물
    public record GeminiResponse(String title, String summary) {}

    // 2. Gemini API 응답 구조 매핑용 (내부 클래스)
    record GeminiApiResponse(List<Candidate> candidates) {}
    record Candidate(Content content) {}
    record Content(List<Part> parts) {}
    record Part(String text) {}
}