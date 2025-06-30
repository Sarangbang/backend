package sarangbang.site.challengeapplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.service.ChallengeService;
import sarangbang.site.challengeapplication.dto.ChallengeJoinDTO;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.challengeapplication.enums.Status;
import sarangbang.site.challengeapplication.exception.DuplicateApplicationException;
import sarangbang.site.challengeapplication.repository.ChallengeApplicationRepository;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeApplicationService {

    private final ChallengeApplicationRepository challengeApplicationRepository;
    private final UserService userService;
    private final ChallengeService challengeService;

    public ChallengeJoinDTO saveChallengeApplication(ChallengeJoinDTO challengeJoinDTO, String userId) {
        log.info("=> 챌린지 신청서 저장 로직 시작. userId: {}, challengeId: {}", userId, challengeJoinDTO.getChallengeId());

        User user = userService.getUserById(userId);

        Challenge challenge = challengeService.getChallengeById(challengeJoinDTO.getChallengeId());
        log.debug("... 사용자 및 챌린지 엔티티 조회 완료");

        // DuplicateApplicationException 예외 처리
        if (challengeApplicationRepository.existsByUserAndChallenge(user, challenge)) {
            log.warn("!! 이미 참여 신청한 챌린지. userId: {}, challengeId: {}", userId, challenge.getId());
            throw new DuplicateApplicationException("이미 참여 신청한 챌린지입니다.");
        }

        ChallengeApplication challengeApplication = new ChallengeApplication(
                challengeJoinDTO.getIntroduction(),
                challengeJoinDTO.getReason(),
                challengeJoinDTO.getCommitment(),
                Status.PENDING,
                challengeJoinDTO.getComment(),
                user,
                challenge
        );
        log.debug("... 신청서 엔티티 생성 완료. 상태를 PENDING으로 설정.");

            log.info("... 챌린지 신청서 DB 저장 완료. applicationId: {}", challengeApplication.getId());
            challengeApplicationRepository.save(challengeApplication);

        ChallengeJoinDTO responseDTO = new ChallengeJoinDTO(
                challengeApplication.getIntroduction(),
                challengeApplication.getReason(),
                challengeApplication.getCommitment(),
                challengeApplication.getStatus(),
                challengeApplication.getComment(),
                challengeApplication.getChallenge().getId()
        );
        log.info("<= 챌린지 신청서 저장 로직 종료. applicationId: {}", challengeApplication.getId());

        return responseDTO;
    }
}
