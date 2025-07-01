package sarangbang.site.challengemember.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.persistence.EntityNotFoundException;
import sarangbang.site.challengemember.dto.ChallengeMemberDTO;
import sarangbang.site.challengemember.service.ChallengeMemberService;

import java.util.List;

@RestController
@RequestMapping("/api/challenge-members")
@RequiredArgsConstructor
@Slf4j
public class ChallengeMemberController {

    private final ChallengeMemberService challengeMemberService;

    /**
     * 챌린지 멤버 목록 조회 API
     */
    @GetMapping("/{challengeId}")
    public ResponseEntity<List<ChallengeMemberDTO>> getMembersByChallengeId(@PathVariable Long challengeId) {
        try {
            List<ChallengeMemberDTO> responseDto = challengeMemberService.getMembersByChallengeId(challengeId);
            ResponseEntity<List<ChallengeMemberDTO>> response = ResponseEntity.ok(responseDto);
            return response;

        } catch (IllegalArgumentException e) {
            log.error("잘못된 챌린지 ID - challengeId: {}, 에러: {}", challengeId, e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (EntityNotFoundException e) {
            log.error("챌린지를 찾을 수 없음 - challengeId: {}, 에러: {}", challengeId, e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("서버 오류 - challengeId: {}, 에러: {}", challengeId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
