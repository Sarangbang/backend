package sarangbang.site.challengemember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.repository.ChallengeRepository;
import sarangbang.site.challengemember.dto.ChallengeMemberResponseDTO;
import sarangbang.site.challengemember.entity.ChallengeMember;
import sarangbang.site.challengemember.repository.ChallengeMemberRepository;
import sarangbang.site.challengemember.dto.ChallengeMemberDTO;
import sarangbang.site.challengeverification.entity.ChallengeVerification;
import sarangbang.site.challengeverification.repository.ChallengeVerificationRepository;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeMemberService {

    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeRepository challengeRepository;
    private final UserService userService;
    private final ChallengeVerificationRepository challengeVerificationRepository;

    // 챌린지 오너 저장
    public void saveChallengeOwner(String userId, Long challengeId) {

        User user = userService.getUserById(userId);
        log.debug("챌린지 등록 멤버 Id : {}", user.getId());

        Challenge challenge = challengeRepository.findChallengeById(challengeId);
        log.debug("챌린지 등록 챌린지 Id : {}", challenge.getId());

        ChallengeMember challengeMember = new ChallengeMember("owner", challenge, user);

        challengeMemberRepository.save(challengeMember);
    }

    // 챌린지 멤버 목록 조회
    public List<ChallengeMemberDTO> getMembersByChallengeId(Long challengeId, LocalDate date, String userId) {

        validateMember(challengeId, userId);

        List<ChallengeMember> members = challengeMemberRepository.findByChallengeId(challengeId);
        List<ChallengeMemberDTO> memberDTOs = new ArrayList<>();

        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59);

        for (ChallengeMember member : members) {

            Optional<ChallengeVerification> verification =
                    challengeVerificationRepository.findByChallenge_IdAndUser_IdAndVerifiedAtBetween(challengeId, member.getUser().getId(), startDate, endDate);

            boolean isVerified = verification.isPresent();

            ChallengeMemberDTO dto = new ChallengeMemberDTO(
                    member.getChallengeMemberId(),
                    member.getUser().getNickname(),
                    member.getRole(),
                    member.getChallenge().getTitle(),
                    member.getChallenge().getMethod(),
                    isVerified
            );

            memberDTOs.add(dto);
        }

        return memberDTOs;
    }

    // 챌린지 멤버인지 확인
    public void validateMember(Long challengeId, String userId) {
        User user = userService.getUserById(userId);
        boolean isMember = challengeMemberRepository.existsByChallengeIdAndUser(challengeId, user);
        if (!isMember) {
            throw new IllegalArgumentException("해당 챌린지에 참가하지 않은 사용자입니다.");
        }
    }

    // 챌린지 멤버 저장
    public void saveChallengeMember(String userId, Long challengeId) {
        User user = userService.getUserById(userId);
        log.debug("챌린지 등록 멤버 Id : {}", user.getId());

        Challenge challenge = challengeRepository.findChallengeById(challengeId);
        log.debug("챌린지 등록 챌린지 Id : {}", challenge.getId());

        ChallengeMember challengeMember = new ChallengeMember("member", challenge, user);

        challengeMemberRepository.save(challengeMember);
    }

    // 특정 챌린지 id 의 member 조회
    public Optional<ChallengeMember> getMemberByChallengeId(String userId, Long challengeId) {
        return challengeMemberRepository.findChallengeMemberByUser_IdAndChallenge_Id(userId, challengeId);
    }

    // 내가 가입한 챌린지 목록 조회
    public List<ChallengeMemberResponseDTO> getChallengesByUserId(String userId, String role) {

        List<ChallengeMember> challengeMembers;

        if(role == null || role.isBlank()) {
            challengeMembers = challengeMemberRepository.findByUser_Id(userId);
        } else {
            challengeMembers = challengeMemberRepository.findByUser_IdAndRole(userId, role);
        }

        if(challengeMembers.isEmpty()) {
            return Collections.emptyList();
        }

        List<ChallengeMemberResponseDTO> dto = new ArrayList<>();
        List<Long> challengeIds = challengeMembers.stream().map(cm -> cm.getChallenge().getId()).collect(Collectors.toList());
        List<Challenge> challenges = challengeRepository.findChallengesByIdIn(challengeIds);

        for(Challenge challenge : challenges) {
            int currentParticipant = challengeMemberRepository.countByChallengeId(challenge.getId());
            Optional<ChallengeMember> mem = getMemberByChallengeId(userId, challenge.getId());
            dto.add(new ChallengeMemberResponseDTO(
                    challenge,
                    currentParticipant,
                    mem.get().getRole()
            ));
        }

        return dto;
    }
}
