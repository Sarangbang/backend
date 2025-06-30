package sarangbang.site.challengeapplication.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.challengeapplication.dto.ChangeChallengeAppDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.challengeapplication.dto.ChallengeJoinDTO;
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
            @ApiResponse(responseCode = "200", description = "정상적으로 처리됨"),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "챌린지 방장이 아님"),
            @ApiResponse(responseCode = "409", description = "이미 처리된 신청서거나 존재하는 멤버인 경우"),
            @ApiResponse(responseCode = "500", description = "예기치 못한 오류 발생")
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
            String message = e.getMessage();
            log.error("챌린지 요청 수락/거부 실패 - 요청자 : {}, 오류 : {}", userDetails.getId(), message);

            if(message.contains("방장")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else if(message.contains("이미")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else {
                return ResponseEntity.badRequest().build();
            }

        }
    }

    @PostMapping
    public ResponseEntity<ChallengeJoinDTO> joinChallenge(@RequestBody @Valid ChallengeJoinDTO challengeJoinDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userId = userDetails.getId();
        log.info("=> 챌린지 참여 요청. challengeId: {}, userId: {}", challengeJoinDTO.getChallengeId(), userId);

        ChallengeJoinDTO requestDTO = challengeApplicationService.saveChallengeApplication(challengeJoinDTO, userId);
        log.info("<= 챌린지 참여 처리 성공. challengeId: {}, userId: {}", challengeJoinDTO.getChallengeId(), userId);

        ResponseEntity<ChallengeJoinDTO> response = ResponseEntity.ok(requestDTO);
        return response;
    }
}

