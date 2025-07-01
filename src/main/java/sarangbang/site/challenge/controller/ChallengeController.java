package sarangbang.site.challenge.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.challenge.dto.ChallengeDTO;
import sarangbang.site.challenge.dto.ChallengeResponseDto;
import sarangbang.site.challenge.service.ChallengeService;
import sarangbang.site.security.details.CustomUserDetails;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
@Slf4j
public class ChallengeController {

    private final ChallengeService challengeService;

    // 신규 챌린지 등록
    @PostMapping
    public ResponseEntity<ChallengeDTO> saveChallenge(@RequestBody ChallengeDTO challengeDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            log.debug("챌린지 등록 요청 : {}, 요청자 : {}", challengeDTO, userDetails.getId());
            ChallengeDTO saveChallenge = challengeService.saveChallenge(challengeDTO, userDetails.getId());
            return ResponseEntity.ok(saveChallenge);

        } catch (IllegalArgumentException e) {
            log.error("챌린지 등록 입력값 오류 - 요청자 : {}, 오류 : {}", userDetails.getId(), e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("챌린지 등록 실패 - 요청자 : {}, 오류 : {}", userDetails.getId(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 전체 챌린지 목록 조회 API
     */
    @GetMapping("/all")
    public ResponseEntity<Page<ChallengeResponseDto>> getAllChallenges(@PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<ChallengeResponseDto> responseDto = challengeService.getAllChallenges(pageable);
            ResponseEntity<Page<ChallengeResponseDto>> response = ResponseEntity.ok(responseDto);
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
    public ResponseEntity<Page<ChallengeResponseDto>> getChallengesByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<ChallengeResponseDto> responseDto = challengeService.getChallengesByCategoryId(categoryId, pageable);
            ResponseEntity<Page<ChallengeResponseDto>> response = ResponseEntity.ok(responseDto);
            return response;

        } catch (Exception e) {
            log.error("카테고리별 챌린지 조회 실패 - categoryId: {}, 에러: {}", categoryId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
