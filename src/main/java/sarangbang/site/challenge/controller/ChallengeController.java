package sarangbang.site.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.challenge.dto.ChallengeDTO;
import sarangbang.site.challenge.dto.ChallengeDetailResponseDto;
import sarangbang.site.challenge.dto.ChallengePopularityResponseDTO;
import sarangbang.site.challenge.dto.ChallengeResponseDto;
import sarangbang.site.challenge.service.ChallengeService;
import sarangbang.site.region.exception.RegionNotFoundException;
import sarangbang.site.security.details.CustomUserDetails;

import java.util.List;
import java.util.Map;

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveChallenge(@RequestPart("challengeDTO") ChallengeDTO challengeDTO, @RequestPart(value = "imageFile", required = false) MultipartFile imageFile, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            log.debug("챌린지 등록 요청 : {}, 요청자 : {}", challengeDTO, userDetails.getId());
            ChallengeDTO saveChallenge = challengeService.saveChallenge(challengeDTO, userDetails.getId(), imageFile);
            return ResponseEntity.ok(saveChallenge);

        } catch (IllegalArgumentException | RegionNotFoundException e) {
            log.error("챌린지 등록 입력값 오류 - 요청자 : {}, 오류 : {}", userDetails.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("중복 챌린지 생성 오류 - 요청자 : {}, 오류 : {}", userDetails.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 전체 챌린지 목록 조회 API
     */
    @Operation(summary = "전체 챌린지 목록 조회", description = "페이지네이션을 적용하여 전체 챌린지 목록을 조회합니다.")
    @GetMapping("/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 목록 조회 성공 (페이지 형식으로 반환)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
    })
    public ResponseEntity<Page<ChallengeResponseDto>> getAllChallenges(
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ChallengeResponseDto> responseDto = challengeService.getAllChallenges(pageable);
        ResponseEntity<Page<ChallengeResponseDto>> response = ResponseEntity.ok(responseDto);
        return response;
    }

    /**
     * 카테고리별 챌린지 목록 조회 API
     */
    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "카테고리별 챌린지 목록 조회", description = "카테고리 ID를 이용하여 해당 카테고리에 속한 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리별 챌린지 목록 조회 성공 (페이지 형식으로 반환)"),
            @ApiResponse(responseCode = "404", description = "해당 카테고리를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
    })
    public ResponseEntity<Page<ChallengeResponseDto>> getChallengesByCategory(
            @PathVariable Long categoryId,
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ChallengeResponseDto> responseDto = challengeService.getChallengesByCategoryId(categoryId, pageable);
        ResponseEntity<Page<ChallengeResponseDto>> response = ResponseEntity.ok(responseDto);
        return response;
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

    // 챌린지 인기순 조회
    @Operation(summary = "챌린지 인기순 조회", description = "인기순으로 챌린지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 인기순 조회 성공"),
            @ApiResponse(responseCode = "500", description = "챌린지 인기순 조회 실패")
    })
    @GetMapping("/popularity")
    public ResponseEntity<List<ChallengePopularityResponseDTO>> getChallengePopularity() {
        List<ChallengePopularityResponseDTO> dtos = challengeService.getChallengePopularity();
        return ResponseEntity.ok(dtos);
    }
}
