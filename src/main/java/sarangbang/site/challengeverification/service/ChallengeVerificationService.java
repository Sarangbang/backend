package sarangbang.site.challengeverification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.service.ChallengeService;
import sarangbang.site.challengemember.service.ChallengeMemberService;
import sarangbang.site.challengeverification.dto.ChallengeVerificationDTO;
import sarangbang.site.challengeverification.entity.ChallengeVerification;
import sarangbang.site.challengeverification.enums.ChallengeVerificationStatus;
import sarangbang.site.challengeverification.repository.ChallengeVerificationRepository;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeVerificationService {

    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final ChallengeService challengeService;
    private final ChallengeMemberService challengeMemberService;
    private final UserService userService;

    public ChallengeVerificationDTO createVerification(String userId, ChallengeVerificationDTO dto) {

        // 1. 챌린지 존재 확인
        Challenge challenge = challengeService.getChallengeById(dto.getChallengeId());

        // 2. 사용자 조회 (ID로 조회)
        User user = userService.getUserById(userId);

        // 3. 챌린지 멤버인지 확인
        challengeMemberService.validateMember(dto.getChallengeId(), userId);

        // 4. 오늘 이미 인증했는지 확인
        validateDailyVerification(challenge, user);

        // 5. 인증 엔티티 생성 및 저장
        ChallengeVerification verification = new ChallengeVerification(LocalDateTime.now(),
                dto.getImgUrl(), dto.getContent(), ChallengeVerificationStatus.APPROVED, null,
                challenge, user
        );

        ChallengeVerification savedVerification = challengeVerificationRepository.save(verification);

        // 6. 응답 DTO 생성
        return new ChallengeVerificationDTO(
                savedVerification.getChallenge().getId(),
                savedVerification.getImgUrl(),
                savedVerification.getContent(),
                savedVerification.getStatus(),
                savedVerification.getUser().getId()
        );
    }

    // 하루 인증 여부 확인
    public void validateDailyVerification(Challenge challenge, User user) {
        LocalDate today = LocalDate.now();
        boolean alreadyVerified = challengeVerificationRepository
                .existsByChallengeAndUserAndCreatedAtBetween(
                        challenge, user, today.atStartOfDay(), today.atTime(23, 59, 59)
                );
        if (alreadyVerified) {
            throw new IllegalArgumentException("오늘 이미 인증을 완료했습니다.");
        }
    }
}