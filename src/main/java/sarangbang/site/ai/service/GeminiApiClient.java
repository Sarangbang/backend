package sarangbang.site.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gemini API 직접 호출 클라이언트
 * 참고: https://ai.google.dev/gemini-api/docs/get-started/rest
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiApiClient {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.base-url}")
    private String baseUrl;

    @Value("${gemini.api.models.chat}")
    private String chatModel;

    @Value("${gemini.api.timeout:30s}")
    private Duration timeout;

    /**
     * 프롬프트를 사용하여 Gemini API로부터 텍스트 콘텐츠를 생성합니다.
     *
     * @param prompt 텍스트 생성을 위한 프롬프트
     * @return 생성된 텍스트 Mono
     */
    public Mono<String> generateText(String prompt) {
        log.info("Gemini API 텍스트 생성 요청: model={}, 프롬프트 길이={}", chatModel, prompt.length());

        WebClient webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Map<String, Object> requestBody = createTextGenerationRequest(prompt);

        String endpoint = String.format("/models/%s:generateContent?key=%s", chatModel, apiKey);

        return webClient.post()
                .uri(endpoint)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(timeout)
                .doOnNext(response -> {
                    log.info("Gemini API 텍스트 생성 응답 수신: 응답 크기={} bytes", response.length());
                    log.debug("텍스트 생성 응답 내용: {}", response);
                })
                .map(this::extractTextFromResponse)
                .doOnError(WebClientResponseException.class, error -> {
                    log.error("Gemini API 텍스트 생성 HTTP 오류: status={}, body={}", error.getStatusCode(), error.getResponseBodyAsString());
                })
                .doOnError(error -> log.error("Gemini API 텍스트 생성 실패: ", error));
    }

    /**
     * 텍스트 생성 요청 본문 생성
     */
    private Map<String, Object> createTextGenerationRequest(String prompt) {
        Map<String, Object> request = new HashMap<>();

        // contents 배열
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", List.of(part));
        request.put("contents", List.of(content));

        // generationConfig
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 1000);
        request.put("generationConfig", generationConfig);

        log.debug("Gemini API 텍스트 생성 요청 본문: {}", request);
        return request;
    }

    /**
     * Gemini API 응답에서 텍스트 추출
     */
    private String extractTextFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);

            log.debug("Gemini API 응답 구조 분석 시작");

            JsonNode candidates = jsonNode.get("candidates");
            if (candidates != null && candidates.isArray() && !candidates.isEmpty()) {
                JsonNode content = candidates.get(0).get("content");
                if (content != null) {
                    JsonNode parts = content.get("parts");
                    if (parts != null && parts.isArray()) {
                        for (JsonNode part : parts) {
                            JsonNode textNode = part.get("text");
                            if (textNode != null) {
                                String extractedText = textNode.asText().trim();
                                log.info("텍스트 추출 완료: 추출된 텍스트='{}'", extractedText);
                                return extractedText;
                            }
                        }
                    }
                }
            }

            log.error("Gemini API 응답에서 텍스트를 찾을 수 없습니다: {}", response);
            throw new RuntimeException("Gemini API 응답이 올바르지 않습니다.");

        } catch (Exception e) {
            log.error("Gemini API 응답 파싱 오류: ", e);
            throw new RuntimeException("Gemini API 응답 파싱 실패: " + e.getMessage());
        }
    }
} 