package sarangbang.site.challengeverification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.service.ChallengeService;
import sarangbang.site.challengemember.dto.ChallengeMemberResponseDTO;
import sarangbang.site.challengemember.entity.ChallengeMember;
import sarangbang.site.challengemember.service.ChallengeMemberService;
import sarangbang.site.challengeverification.dto.ChallengeVerificationRequestDTO;
import sarangbang.site.challengeverification.dto.ChallengeVerificationResponseDTO;
import sarangbang.site.challengeverification.dto.TodayVerificationStatusResponseDTO;
import sarangbang.site.challengeverification.entity.ChallengeVerification;
import sarangbang.site.challengeverification.enums.ChallengeVerificationStatus;
import sarangbang.site.challengeverification.repository.ChallengeVerificationRepository;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeVerificationService {

    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final ChallengeService challengeService;
    private final ChallengeMemberService challengeMemberService;
    private final UserService userService;

    public ChallengeVerificationResponseDTO createVerification(String userId, ChallengeVerificationRequestDTO dto) {

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
        ChallengeVerificationResponseDTO responseDTO = new ChallengeVerificationResponseDTO(
                savedVerification.getChallenge().getId(),
                savedVerification.getImgUrl(),
                savedVerification.getContent(),
                savedVerification.getStatus(),
                savedVerification.getUser().getId()
        );

        return responseDTO;
    }

    // 하루 인증 여부 확인
    public void validateDailyVerification(Challenge challenge, User user) {
        LocalDate today = LocalDate.now();
        log.debug("일일 인증 중복 검사 시작 - 사용자: {}, 챌린지명: {}, 날짜: {}",
                user.getId(), challenge.getId(), today);
        
        boolean alreadyVerified = challengeVerificationRepository
                .existsByChallengeAndUserAndCreatedAtBetween(
                        challenge, user, today.atStartOfDay(), today.atTime(23, 59, 59)
                );
        
        if (alreadyVerified) {
            log.warn("일일 인증 중복 시도 차단 - 사용자: {}, 챌린지명: {}, 날짜: {}",
                    user.getId(), challenge.getId(), today);
            throw new IllegalArgumentException("오늘 이미 인증을 완료했습니다.");
        }
        
        log.debug("일일 인증 중복 검사 통과 - 사용자: {}, 챌린지: {}", 
                user.getId(), challenge.getId());
    }

    // 금일 챌린지 인증 내역
    public List<TodayVerificationStatusResponseDTO> getTodayVerifications(String userId) {

        LocalDate today = LocalDate.now();

        List<ChallengeMemberResponseDTO> challengeLists = challengeMemberService.getChallengesByUserId(userId, null); // 참여한 모든 챌린지
        List<ChallengeVerification> verifications = challengeVerificationRepository.findChallengeVerificationsByUser_IdAndVerifiedAt(
                userId, today.atTime(23, 59, 59)); // 오늘 인증한 모든 챌린지

        Set<Long> todayVerificationIds =
                verifications.stream().map(v -> v.getChallenge().getId()).collect(Collectors.toSet());

        List<TodayVerificationStatusResponseDTO> dtos = challengeLists.stream()
                .map(challenge -> new TodayVerificationStatusResponseDTO(
                        1,
                        challenge.getTitle(),
                        challenge.getLocation(),
                        challenge.getImage(),
                        challenge.getParticipants(),
                        challenge.getCurrentParticipants(),
                        todayVerificationIds.contains(challenge.get)
                ))
                .collect(Collectors.toList());


        return dtos;
    }
}