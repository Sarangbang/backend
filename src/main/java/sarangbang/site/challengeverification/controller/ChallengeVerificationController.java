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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.challengeverification.dto.ChallengeVerificationByDateDTO;
import sarangbang.site.challengeverification.dto.ChallengeVerificationRequestDTO;
import sarangbang.site.challengeverification.dto.ChallengeVerificationResponseDTO;
import sarangbang.site.challengeverification.dto.TodayVerificationStatusResponseDTO;
import sarangbang.site.challengeverification.service.ChallengeVerificationService;
import sarangbang.site.security.details.CustomUserDetails;

import java.time.LocalDate;
import java.util.List;

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
                            ChallengeVerificationResponseDTO.class))
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
    
    public ResponseEntity<ChallengeVerificationResponseDTO> createVerification(
            @RequestBody @Valid ChallengeVerificationRequestDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            String userId = userDetails.getId();
            log.info("챌린지 인증 등록 요청 - 사용자: {}, 챌린지ID: {}", userId, dto.getChallengeId());
            ChallengeVerificationResponseDTO result = challengeVerificationService.createVerification(userId, dto);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("챌린지 인증 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /* 특정 챌린지 날짜별 인증 조회 */
    @Operation(summary = "특정 챌린지 날짜별 인증 조회", description = "특정 챌린지를 날짜별로 인증을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 챌린지 날짜별 인증 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChallengeVerificationByDateDTO.class))),
            @ApiResponse(responseCode = "400", description = "해당 챌린지 인증을 찾을 수 없음", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{challengeId}")
    public ResponseEntity<List<ChallengeVerificationByDateDTO>> getVerificationByDate(
            @PathVariable Long challengeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChallengeVerificationByDateDTO> responseDTOList =
                challengeVerificationService.getChallengeVerificationByDate(challengeId, selectedDate, userDetails.getId());

        ResponseEntity<List<ChallengeVerificationByDateDTO>> response = ResponseEntity.ok(responseDTOList);
        return response;
    }

    // 금일 챌린지 인증 내역
    @Operation(summary = "금일 챌린지 인증 여부", description = "내가 참여한 모든 챌린지의 금일 인증 여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "금일 챌린지 인증 내역 확인 완료", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TodayVerificationStatusResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "금일 챌린지 인증 내역 확인 실패", content = @Content)

    })
    @GetMapping("/status")
    public ResponseEntity<List<TodayVerificationStatusResponseDTO>> getTodayVerifications(@AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            String userId = userDetails.getId();
            List<TodayVerificationStatusResponseDTO> dto = challengeVerificationService.getTodayVerifications(userId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}