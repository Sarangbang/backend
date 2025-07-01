package sarangbang.site.challengeapplication.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.challengeapplication.dto.ChallengeJoinDTO;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.challengeapplication.exception.ChallengeApplicationExceptionHandler;
import sarangbang.site.challengeapplication.service.ChallengeApplicationService;
import sarangbang.site.security.details.CustomUserDetails;

@Tag(name = "Challenge Application", description = "챌린지 신청 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge/application")
public class ChallengeApplicationController {

    private final ChallengeApplicationService challengeApplicationService;

    @PostMapping
    @Operation(summary = "챌린지 신청", description = "사용자가 원하는 챌린지를 선택하여 신청")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "챌린지 신청 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChallengeJoinDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "챌린지 중복 신청",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChallengeApplicationExceptionHandler.ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<ChallengeJoinDTO> joinChallenge(@RequestBody @Valid ChallengeJoinDTO challengeJoinDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userId = userDetails.getId();
        log.info("=> 챌린지 참여 요청. challengeId: {}, userId: {}", challengeJoinDTO.getChallengeId(), userId);

        ChallengeJoinDTO requestDTO = challengeApplicationService.saveChallengeApplication(challengeJoinDTO, userId);
        log.info("<= 챌린지 참여 처리 성공. challengeId: {}, userId: {}", challengeJoinDTO.getChallengeId(), userId);

        ResponseEntity<ChallengeJoinDTO> response = ResponseEntity.ok(requestDTO);
        return response;
    }
}