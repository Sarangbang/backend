package sarangbang.site.challengeverification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Challenge Verification", description = "챌린지 인증 관련 API")
public class ChallengeVerificationController {

    private final ChallengeVerificationService challengeVerificationService;

    @PostMapping
    @Operation(summary = "챌린지 인증", description = "챌린지 참가자가 인증을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "인증 등록 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            ChallengeVerificationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 요청 (필수 필드 누락, 형식 오류)",
                    content = @Content
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "충돌 (중복 인증)",
                    content = @Content
            )
    })
    
    public ResponseEntity<ChallengeVerificationDTO> createVerification(
            @RequestBody @Valid ChallengeVerificationDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            String userId = userDetails.getId();
            log.info("챌린지 인증 등록 요청 - 사용자: {}, 챌린지ID: {}", userId, dto.getChallengeId());
            ChallengeVerificationDTO result = challengeVerificationService.createVerification(userId, dto);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("챌린지 인증 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}