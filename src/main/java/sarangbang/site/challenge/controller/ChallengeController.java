package sarangbang.site.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.challenge.dto.ChallengeDTO;
import sarangbang.site.challenge.dto.ChallengeDetailResponseDto;
import sarangbang.site.challenge.service.ChallengeService;
import sarangbang.site.security.details.CustomUserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sarangbang.site.challenge.dto.ChallengeResponseDto;
import java.util.List;

@Tag(name = "Challenge", description = "챌린지 관련 API")
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
    public ResponseEntity<?> getChallengeDetails(@PathVariable Long challengeId) {
        try {
            ChallengeDetailResponseDto responseDto = challengeService.getChallengeDetails(challengeId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            log.error("챌린지 조회 실패 - ID: {}, 에러: {}", challengeId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
