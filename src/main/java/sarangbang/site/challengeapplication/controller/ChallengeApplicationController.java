package sarangbang.site.challengeapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.challengeapplication.dto.ChangeChallengeAppDTO;
import sarangbang.site.challengeapplication.service.ChallengeApplicationService;
import sarangbang.site.security.details.CustomUserDetails;

@Tag(name = "challenge-application", description = "챌린지 신청서 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge/application")
public class ChallengeApplicationController {

    private final ChallengeApplicationService challengeApplicationService;

    // 챌린지 신청 수락/거부
    @Operation(summary = "챌린지 신청 수락/거부", description = "방장이 챌린지 신청서를 확인하고 참가 여부를 결정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 참가여부 성공"),
            @ApiResponse(responseCode = "401", description = "챌린지 참가여부 실패")
    })
    @PostMapping("/{appId}")
    public ResponseEntity<ChangeChallengeAppDTO> changeApplicationStatus(
            @PathVariable Long appId, @RequestBody @Valid ChangeChallengeAppDTO changeChallengeAppDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        try{
            log.debug("수락/거부 타겟 챌린지 Id : {}, 승인자 : {}", appId, userDetails.getId());
            ChangeChallengeAppDTO changeApp = challengeApplicationService.changeApplicationStatus(appId, changeChallengeAppDTO, userDetails.getId());
            return ResponseEntity.ok(changeApp);

        } catch (IllegalArgumentException e){
            log.error("챌린지 요청 수락/거부 입력값 오류 - 요청자 : {}, 오류 : {}", userDetails.getId(), e.getMessage());
            return ResponseEntity.badRequest().build();

        }
    }
}
