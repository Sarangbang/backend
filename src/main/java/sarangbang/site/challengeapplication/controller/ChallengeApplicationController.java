package sarangbang.site.challengeapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.challengeapplication.dto.ChangeChallengeAppDTO;
import sarangbang.site.challengeapplication.dto.ChallengeApplicationDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.challengeapplication.dto.ChallengeJoinDTO;
import sarangbang.site.challengeapplication.dto.MyPageApplicationDTO;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.challengeapplication.exception.ChallengeApplicationExceptionHandler;
import sarangbang.site.challengeapplication.service.ChallengeApplicationService;
import sarangbang.site.security.details.CustomUserDetails;

import java.util.List;

@Tag(name = "Challenge-application", description = "챌린지 신청서 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge/application")
public class ChallengeApplicationController {

    private final ChallengeApplicationService challengeApplicationService;

    // 챌린지 신청 수락/거부
    @Operation(summary = "챌린지 신청 수락/거부", description = "방장이 챌린지 신청서를 확인하고 참가 여부를 결정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 처리됨", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangeChallengeAppDTO.class))),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 잘못된 요청", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "챌린지 방장이 아님", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "이미 처리된 신청서거나 존재하는 멤버인 경우", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/{appId}")
    public ResponseEntity<ChangeChallengeAppDTO> changeApplicationStatus(
            @PathVariable Long appId, @RequestBody @Valid ChangeChallengeAppDTO changeChallengeAppDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        try{
            log.debug("수락/거부 타겟 챌린지 Id : {}, 승인자 : {}", appId, userDetails.getId());
            ChangeChallengeAppDTO changeApp = challengeApplicationService.changeApplicationStatus(appId, changeChallengeAppDTO, userDetails.getId());
            return ResponseEntity.ok(changeApp);
        } catch (IllegalStateException e){
            log.error("챌린지 요청 수락/거부 실패. 신청서 Id: {}, 오류 : {}", appId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            log.error("챌린지 요청 수락/거부 실패. 신청서 Id: {}, 오류 : {}", appId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SecurityException e){
            log.error("챌린지 요청 수락/거부 실패. 신청서 Id: {}, 오류 : {}", appId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

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


    @GetMapping("/manage/{challengeId}")
    @Operation(summary = "챌린지 참여 신청 목록 조회", description = "방장이 특정 챌린지의 참여 신청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "신청 목록 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChallengeApplicationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "방장 권한 없음",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "챌린지를 찾을 수 없음",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<List<ChallengeApplicationDTO>> getChallengeApplications(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            log.info("=> 챌린지 참여 신청 목록 조회 요청. challengeId: {}, ownerId: {}", challengeId, userDetails.getId());
            
            List<ChallengeApplicationDTO> applications = challengeApplicationService
                    .getChallengeApplications(challengeId, userDetails.getId());
            
            log.info("<= 챌린지 참여 신청 목록 조회 성공. challengeId: {}, 신청 개수: {}", challengeId, applications.size());
            return ResponseEntity.ok(applications);
            
        } catch (IllegalArgumentException e) {
            log.error("챌린지 참여 신청 목록 조회 실패. challengeId: {}, 오류: {}", challengeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            log.error("챌린지 참여 신청 목록 조회 권한 없음. challengeId: {}, 오류: {}", challengeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/detail/{applicationId}")
    @Operation(summary = "챌린지 신청서 상세 조회", description = "방장이 특정 신청서의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "신청서 상세 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChallengeApplicationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "방장 권한 없음",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "신청서를 찾을 수 없음",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<ChallengeApplicationDTO> getChallengeApplicationDetail(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            log.info("=> 챌린지 신청서 상세 조회 요청. applicationId: {}, ownerId: {}", applicationId, userDetails.getId());
            
            ChallengeApplicationDTO application = challengeApplicationService
                    .getChallengeApplicationDetail(applicationId, userDetails.getId());
            
            log.info("<= 챌린지 신청서 상세 조회 성공. applicationId: {}", applicationId);
            return ResponseEntity.ok(application);
            
        } catch (IllegalArgumentException e) {
            log.error("챌린지 신청서 상세 조회 실패. applicationId: {}, 오류: {}", applicationId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            log.error("챌린지 신청서 상세 조회 권한 없음. applicationId: {}, 오류: {}", applicationId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/my-applications")
    @Operation(
            summary = "내 챌린지 신청내역 조회",
            description = "로그인한 사용자의 모든 챌린지 신청내역을 조회합니다. " +
                        "모든 상태(대기/승인/거절)의 신청서를 포함하며, " +
                        "프론트엔드에서 상태별 탭 필터링을 수행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "신청내역 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MyPageApplicationDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MyPageApplicationDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<List<MyPageApplicationDTO>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            String userId = userDetails.getId();
            log.info("=> 내 챌린지 신청내역 조회 요청. userId: {}", userId);

            List<MyPageApplicationDTO> applications = challengeApplicationService
                    .getMyApplications(userId);

            log.info("<= 내 챌린지 신청내역 조회 성공. userId: {}, 건수: {}", userId, applications.size());
            return ResponseEntity.ok(applications);

        } catch (Exception e) {
            log.error("내 챌린지 신청내역 조회 실패. 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}

