package sarangbang.site.challenge.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.challenge.dto.ChallengeResponseDto;
import sarangbang.site.challenge.service.ChallengeService;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
@Slf4j
public class ChallengeController {

    private final ChallengeService challengeService;

    /**
     * 전체 챌린지 목록 조회 API
     */
    @GetMapping("/all")
    public ResponseEntity<List<ChallengeResponseDto>> getAllChallenges() {
        try {
            List<ChallengeResponseDto> responseDto = challengeService.getAllChallenges();
            ResponseEntity<List<ChallengeResponseDto>> response = ResponseEntity.ok(responseDto);
            return response;

        } catch (Exception e) {
            log.error("전체 챌린지 조회 실패 - 에러: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 카테고리별 챌린지 목록 조회 API
     */
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<ChallengeResponseDto>> getChallengesByCategory(@PathVariable Long categoryId) {
        try {
            List<ChallengeResponseDto> responseDto = challengeService.getChallengesByCategoryId(categoryId);
            ResponseEntity<List<ChallengeResponseDto>> response = ResponseEntity.ok(responseDto);
            return response;

        } catch (Exception e) {
            log.error("카테고리별 챌린지 조회 실패 - categoryId: {}, 에러: {}", categoryId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
