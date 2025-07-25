package sarangbang.site.challengeapplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.challengeapplication.dto.ChangeChallengeAppDTO;
import sarangbang.site.challengeapplication.dto.ChallengeApplicationDTO;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.service.ChallengeService;
import sarangbang.site.challengeapplication.dto.ChallengeJoinDTO;
import sarangbang.site.challengeapplication.enums.ChallengeApplyStatus;
import sarangbang.site.challengeapplication.exception.DuplicateApplicationException;
import sarangbang.site.challengeapplication.repository.ChallengeApplicationRepository;
import sarangbang.site.challengemember.entity.ChallengeMember;
import sarangbang.site.challengemember.service.ChallengeMemberService;
import org.springframework.context.ApplicationEventPublisher;
import sarangbang.site.challengeapplication.event.ChallengeMemberAcceptedEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final ApplicationEventPublisher eventPublisher;

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
                Challenge challenge = challengeService.getChallengeById(app.getChallenge().getId());
                User user = userService.getUserById(app.getUser().getId());
                ChallengeMemberAcceptedEvent event = new ChallengeMemberAcceptedEvent(
                        challenge.getId(),
                        user.getId(),
                        user.getNickname()
                );
                eventPublisher.publishEvent(event);
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
                null,
                user,
                challenge
        );

        log.info("... 챌린지 신청서 DB 저장 완료. userId: {}, applicationId: {}", userId, challengeApplication.getId());

        challengeApplicationRepository.save(challengeApplication);

        ChallengeJoinDTO responseDTO = new ChallengeJoinDTO(
                challengeApplication.getIntroduction(),
                challengeApplication.getReason(),
                challengeApplication.getCommitment(),
                challengeApplication.getChallengeApplyStatus(),
                challengeApplication.getChallenge().getId()
        );

        log.info("<= 챌린지 신청서 저장 로직 종료. userId: {}, applicationId: {}", userId, challengeApplication.getId());
        return responseDTO;
    }

    /**
     * 특정 챌린지의 참여 신청 목록 조회 (방장 전용)
     */
    @Transactional(readOnly = true)
    public List<ChallengeApplicationDTO> getChallengeApplications(Long challengeId, String ownerId) {
        log.info("=> 챌린지 참여 신청 목록 조회 시작. challengeId: {}, ownerId: {}", challengeId, ownerId);
        
        // 방장 권한 확인
        validateOwnerPermission(ownerId, challengeId);
        
        List<ChallengeApplication> applications = challengeApplicationRepository
                .findByChallengeIdWithUserAndRegion(challengeId);
        
        List<ChallengeApplicationDTO> result = applications.stream()
                .map(ChallengeApplicationDTO::from)
                .collect(Collectors.toList());
        
        log.info("<= 챌린지 참여 신청 목록 조회 완료. challengeId: {}, 신청 개수: {}", challengeId, result.size());
        return result;
    }

    /**
     * 특정 신청서 상세 조회 (방장 전용)
     */
    @Transactional(readOnly = true)
    public ChallengeApplicationDTO getChallengeApplicationDetail(Long applicationId, String ownerId) {
        log.info("=> 챌린지 신청서 상세 조회 시작. applicationId: {}, ownerId: {}", applicationId, ownerId);
        
        ChallengeApplication application = challengeApplicationRepository
                .findByIdWithUserAndRegion(applicationId);
        
        if (application == null) {
            throw new IllegalArgumentException("신청서를 찾을 수 없습니다. applicationId: " + applicationId);
        }
        
        // 방장 권한 확인
        validateOwnerPermission(ownerId, application.getChallenge().getId());
        
        ChallengeApplicationDTO result = ChallengeApplicationDTO.from(application);
        
        log.info("<= 챌린지 신청서 상세 조회 완료. applicationId: {}", applicationId);
        return result;
    }

    /**
     * 방장 권한 검증
     */
    private void validateOwnerPermission(String ownerId, Long challengeId) {
        Optional<ChallengeMember> member = challengeMemberService.getMemberByChallengeId(ownerId, challengeId);
        
        if (member.isEmpty()) {
            throw new IllegalArgumentException("챌린지 멤버를 찾을 수 없습니다.");
        }
        
        if (!"owner".equals(member.get().getRole())) {
            throw new SecurityException("챌린지 방장만 참여 신청을 조회할 수 있습니다.");
        }
    }
}
