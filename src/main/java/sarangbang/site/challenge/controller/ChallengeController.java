package sarangbang.site.challenge.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.challenge.dto.ChallengeDTO;
import sarangbang.site.challenge.service.ChallengeService;
import sarangbang.site.security.details.CustomUserDetails;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge")
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

}
