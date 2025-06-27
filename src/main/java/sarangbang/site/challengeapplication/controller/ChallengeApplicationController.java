package sarangbang.site.challengeapplication.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.challengeapplication.dto.ChallengeJoinDTO;
import sarangbang.site.challengeapplication.service.ChallengeApplicationService;
import sarangbang.site.security.details.CustomUserDetails;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge/application")
public class ChallengeApplicationController {

    private final ChallengeApplicationService challengeApplicationService;

    @PostMapping
    public ResponseEntity<ChallengeJoinDTO> joinChallenge(@RequestBody ChallengeJoinDTO challengeJoinDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userId = userDetails.getId();
        log.info("=> 챌린지 참여 요청. challengeId: {}, userId: {}", challengeJoinDTO.getChallengeId(), userId);

        ChallengeJoinDTO requestDTO = challengeApplicationService.saveChallengeApplication(challengeJoinDTO, userDetails.getId());
        log.info("<= 챌린지 참여 처리 성공. challengeId: {}, userId: {}", challengeJoinDTO.getChallengeId(), userId);

        ResponseEntity<ChallengeJoinDTO> response = ResponseEntity.ok(requestDTO);
        return response;
    }
}