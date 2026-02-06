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
    private final ObjectMapper objectMapper;

    // --- 1. 초기 분석 및 질문 생성 ---
    public QuestionGenerationResponse analyzeAndGenerateQuestions(String userInput) {
        String prompt = """
                [역할]
                너는 사용자의 경험을 분석하여 기록을 정리하고 깊이 있는 질문을 던지는 에디터야.

                [수행 작업]
                1. 입력된 'content'를 분석해서 다음 4가지를 생성해.
                   - title: 20자 이내의 매력적인 제목
                   - summary: 내용을 한 줄로 요약 (
                   - refined_content: 사용자의 입력을 오타 수정 및 비문 교정하여 깔끔한 줄글로 다듬은 본문
                   - category: 적절한 카테고리 (영어 대문자: DEVELOPMENT, PART_TIME, STUDY, PROJECT, ETC 중 택1)
                2. 사용자의 생각이나 구체적인 근거가 부족한 부분에 대해 면접관 관점의 날카로운 질문을 최대 3개 생성해. (질문이 필요 없으면 빈 배열)

                [입력 데이터]
                { "content": "%s" }

                [출력 형식]
                반드시 아래 JSON 형식으로만 답변해. 마크다운(```json) 없이 순수 JSON만 반환해.
                {
                  "title": "생성된 제목",
                  "summary": "생성된 요약",
                  "refined_content": "다듬어진 본문 내용",
                  "category": "CATEGORY_NAME",
                  "questions": ["질문1", "질문2"]
                }
                """.formatted(userInput);

        return callGemini(prompt, QuestionGenerationResponse.class);
    }

    // --- 2. 답변을 반영하여 본문 업데이트 ---
    public ContentUpdateResponse integrateAnswer(String currentContent, String question, String userAnswer) {
        String prompt = """
                [역할]
                너는 사용자의 경험 기록을 완성해주는 전문 에디터야.

                [수행 작업]
                1. 기존 'content'에 'question'에 대한 사용자의 'answer' 내용을 자연스럽게 녹여내어 전체 본문을 재작성(Overwrite)해.
                2. 답변 내용이 추가되어 글의 깊이가 달라졌을 테니, 이를 반영한 'title'과 'summary'를 새로 생성해.
                3. 없는 내용을 억지로 지어내지 말고, 문맥을 자연스럽게 잇는 데 집중해.

                [입력 데이터]
                {
                    "content": "%s",
                    "question": "%s",
                    "answer": "%s"
                }

                [출력 형식]
                반드시 아래 JSON 형식으로만 답변해. 마크다운 없이 순수 JSON만 반환해.
                {
                  "title": "새로 생성된 제목 (20자 이내)",
                  "summary": "새로 생성된 요약 (한 줄, 존댓말)",
                  "updated_content": "답변이 통합된 전체 본문 내용"
                }
                """.formatted(currentContent, question, userAnswer);

        return callGemini(prompt, ContentUpdateResponse.class);
    }

    // --- 공통 호출 로직 ---
    private <T> T callGemini(String prompt, Class<T> responseType) {
        String url = apiUrl + apiKey;
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        try {
            String responseText = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GeminiApiResponse.class)
                    .candidates().get(0).content().parts().get(0).text();

            String cleanJson = responseText.replace("```json", "").replace("```", "").trim();
            return objectMapper.readValue(cleanJson, responseType);
        } catch (Exception e) {
            log.error("Gemini API Error", e);
            throw new RuntimeException("AI 처리 중 오류가 발생했습니다.");
        }
    }

    // --- DTOs ---
    public record QuestionGenerationResponse(
            String title,
            String summary,
            String refined_content, // JSON의 key 이름과 같아야 함
            String category,
            List<String> questions
    ) {}
    public record ContentUpdateResponse(
            String title,
            String summary,
            String updated_content
    ) {}
    // Gemini API 내부 구조용
    record GeminiApiResponse(List<Candidate> candidates) {}
    record Candidate(Content content) {}
    record Content(List<Part> parts) {}
    record Part(String text) {}
}