package sarangbang.site.challengeapplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.challengeapplication.dto.ChangeChallengeAppDTO;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.service.ChallengeService;
import sarangbang.site.challengeapplication.dto.ChallengeJoinDTO;
import sarangbang.site.challengeapplication.enums.ChallengeApplyStatus;
import sarangbang.site.challengeapplication.exception.DuplicateApplicationException;
import sarangbang.site.challengeapplication.repository.ChallengeApplicationRepository;
import sarangbang.site.challengemember.entity.ChallengeMember;
import sarangbang.site.challengemember.service.ChallengeMemberService;

import java.util.Optional;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeApplicationService {

    private final ChallengeApplicationRepository challengeApplicationRepository;
    private final ChallengeMemberService challengeMemberService;
    private final UserService userService;
    private final ChallengeService challengeService;

    // 챌린지 신청 수락/거부
    @Transactional
    public ChangeChallengeAppDTO changeApplicationStatus(Long appId, ChangeChallengeAppDTO dto, String ownerId) {

        ChallengeApplication app = challengeApplicationRepository.findChallengeApplicationById(appId);
        if(app==null){
            throw new IllegalArgumentException("챌린지 "+appId+"를 찾을 수 없습니다.");
        }
        Optional<ChallengeMember> member = challengeMemberService.getMemberByChallengeId(ownerId, app.getChallenge().getId()); // 특정 챌린지의 id 에서 member 조회
        if(member.isEmpty()){
            throw new IllegalArgumentException("챌린지 멤버 "+member+"를 찾을 수 없습니다.");
        }

        if(!app.getChallengeApplyStatus().equals(ChallengeApplyStatus.PENDING)){
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }

        if(member.get().getRole().equals("owner")) {

            if(dto.getApplyStatus().equals(ChallengeApplyStatus.REJECTED)) {
                app.updateAppStatus(ChallengeApplyStatus.REJECTED);
                app.updateAppComment(dto.getComment());
            } else {
                app.updateAppStatus(ChallengeApplyStatus.APPROVED);
                app.updateAppComment(dto.getComment());
                Optional<ChallengeMember> findMember = challengeMemberService.getMemberByChallengeId(app.getUser().getId(), app.getChallenge().getId());
                if(findMember.isPresent()){
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                }
                challengeMemberService.saveChallengeMember(app.getUser().getId(), app.getChallenge().getId());
            }
            challengeApplicationRepository.save(app);

            ChangeChallengeAppDTO changeApp = new ChangeChallengeAppDTO(app.getChallengeApplyStatus(), app.getComment());
            return changeApp;

        } else throw new SecurityException("챌린지 방장의 권한이 없습니다.");

    }


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
                ChallengeApplyStatus.PENDING,
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
                challengeApplication.getChallengeApplyStatus(),
                challengeApplication.getComment(),
                challengeApplication.getChallenge().getId()
        );
        log.info("<= 챌린지 신청서 저장 로직 종료. applicationId: {}", challengeApplication.getId());

        return responseDTO;
    }
}
