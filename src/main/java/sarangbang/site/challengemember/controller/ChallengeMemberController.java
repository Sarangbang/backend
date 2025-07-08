package sarangbang.site.challengemember.controller;

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
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import sarangbang.site.challengemember.dto.ChallengeMemberDTO;
import sarangbang.site.challengemember.dto.ChallengeMemberResponseDTO;
import sarangbang.site.challengemember.service.ChallengeMemberService;
import sarangbang.site.security.details.CustomUserDetails;

import java.util.List;

@Tag(name = "Challenge-Member", description = "챌린지 참여 멤버 관련 API")
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
            
        }
    }

    // 내가 가입한 챌린지 목록 조회
    @Operation(summary = "가입한 챌린지 목록 조회", description = "내가 가입한 챌린지의 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChallengeMemberResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "서버오류", content = @Content(mediaType = "application/json"))
    })
    @GetMapping()
    public ResponseEntity<List<ChallengeMemberResponseDTO>> getChallengesByUserId(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String role) {
        try {
            String userId = userDetails.getId();
            List<ChallengeMemberResponseDTO> dto = challengeMemberService.getChallengesByUserId(userId, role);
            return  ResponseEntity.ok(dto);
        } catch(IllegalArgumentException e) {
            log.error("가입한 챌린지 목록을 찾을 수 없음 - userId : {}, 에러 : {}", userDetails.getId(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
