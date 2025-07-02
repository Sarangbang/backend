package sarangbang.site.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import sarangbang.site.challenge.dto.ChallengeDetailResponseDto;
import sarangbang.site.challenge.dto.ChallengeResponseDto;
import sarangbang.site.challenge.service.ChallengeService;
import sarangbang.site.security.details.CustomUserDetails;

@Tag(name = "Challenge", description = "챌린지 관련 API")
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
@Slf4j
public class ChallengeController {

    private final ChallengeService challengeService;

    // 신규 챌린지 등록
    @Operation(summary = "신규 챌린지 등록", description = "사용자가 신규 챌린지를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 정상 등록", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChallengeDTO.class))),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<ChallengeDTO> saveChallenge(@RequestBody ChallengeDTO challengeDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            log.debug("챌린지 등록 요청 : {}, 요청자 : {}", challengeDTO, userDetails.getId());
            ChallengeDTO saveChallenge = challengeService.saveChallenge(challengeDTO, userDetails.getId());
            return ResponseEntity.ok(saveChallenge);

        } catch (IllegalArgumentException e) {
            log.error("챌린지 등록 입력값 오류 - 요청자 : {}, 오류 : {}", userDetails.getId(), e.getMessage());
            return ResponseEntity.badRequest().build();

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

    /**
     * 챌린지 상세 정보 조회 API
     */
    @Operation(summary = "챌린지 상세 정보 조회", description = "챌린지 ID를 이용하여 특정 챌린지의 모든 상세 정보 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 상세 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = ChallengeDetailResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 챌린지를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
    })
    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeDetailResponseDto> getChallengeDetails(@PathVariable Long challengeId) {
        try {
            ChallengeDetailResponseDto responseDto = challengeService.getChallengeDetails(challengeId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            log.error("챌린지 조회 실패 - ID: {}, 에러: {}", challengeId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
