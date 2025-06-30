package sarangbang.site.challengeverification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.repository.ChallengeRepository;
import sarangbang.site.challengemember.repository.ChallengeMemberRepository;
import sarangbang.site.challengeverification.dto.ChallengeVerificationDTO;
import sarangbang.site.challengeverification.entity.ChallengeVerification;
import sarangbang.site.challengeverification.repository.ChallengeVerificationRepository;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeVerificationService {

    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final UserRepository userRepository;

    public ChallengeVerificationDTO createVerification(String userId, ChallengeVerificationDTO dto) {

        // 1. 챌린지 존재 확인
        Challenge challenge = challengeRepository.findById(dto.getChallengeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 챌린지입니다."));

        // 2. 사용자 조회 (ID로 조회)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 3. 챌린지 멤버인지 확인
        boolean isMember = challengeMemberRepository.existsByChallengeIdAndUser(dto.getChallengeId(), user);
        if (!isMember) {
            throw new IllegalArgumentException("해당 챌린지에 참가하지 않은 사용자입니다.");
        }

        // 4. 인증 엔티티 생성 및 저장
        ChallengeVerification verification = new ChallengeVerification(null,
                dto.getImgUrl(), dto.getContent(), "PENDING", "", challenge, user
        );

        ChallengeVerification savedVerification = challengeVerificationRepository.save(verification);

        // 5. 응답 DTO 생성
        ChallengeVerificationDTO responseDto = new ChallengeVerificationDTO();
        responseDto.setId(savedVerification.getId());
        responseDto.setChallengeId(savedVerification.getChallenge().getId());
        responseDto.setImgUrl(savedVerification.getImgUrl());
        responseDto.setContent(savedVerification.getContent());
        responseDto.setStatus(savedVerification.getStatus());

        return responseDto;
    }
}