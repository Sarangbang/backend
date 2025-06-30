package sarangbang.site.challengeverification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.challengeverification.dto.ChallengeVerificationDTO;
import sarangbang.site.challengeverification.service.ChallengeVerificationService;
import sarangbang.site.security.details.CustomUserDetails;

@RestController
@RequestMapping("/api/challenge-verifications")
@RequiredArgsConstructor
@Slf4j
public class ChallengeVerificationController {

    private final ChallengeVerificationService challengeVerificationService;

    @PostMapping
    public ResponseEntity<ChallengeVerificationDTO> createVerification(
            @RequestBody ChallengeVerificationDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            String userId = userDetails.getId();
            log.info("챌린지 인증 등록 요청 - 사용자: {}, 챌린지ID: {}", userId, dto.getChallengeId());

            ChallengeVerificationDTO result = challengeVerificationService.createVerification(userId, dto);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("챌린지 인증 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("챌린지 인증 등록 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}