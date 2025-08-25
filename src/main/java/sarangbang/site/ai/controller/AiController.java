package sarangbang.site.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.ai.service.MotivationalMessageService;

@Tag(name = "AI API", description = "AI 관련 기능 API")
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final MotivationalMessageService motivationalMessageService;

    @Operation(summary = "챌린지 동기부여 메시지 생성", description = "주어진 챌린지 ID에 대한 동기부여 메시지를 생성하여 반환합니다.")
    @GetMapping("/motivational-message/{challengeId}")
    public ResponseEntity<String> getMotivationalMessage(@PathVariable Long challengeId) {
        String message = motivationalMessageService.generateMotivationalMessage(challengeId);
        return ResponseEntity.ok(message);
    }
}
