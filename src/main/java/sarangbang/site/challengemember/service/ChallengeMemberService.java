package sarangbang.site.challengemember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.repository.ChallengeRepository;
import sarangbang.site.challengemember.entity.ChallengeMember;
import sarangbang.site.challengemember.repository.ChallengeMemberRepository;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeMemberService {

    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeRepository challengeRepository;
    private final UserService userService;

    // 챌린지 멤버 저장
    public void saveChallengeOwner(String userId, Long challengeId) {

        User user = userService.getUserById(userId);
        log.debug("챌린지 등록 멤버 Id : {}", user.getId());

        Challenge challenge = challengeRepository.findChallengeById(challengeId);
        log.debug("챌린지 등록 챌린지 Id : {}", challenge.getId());

        ChallengeMember challengeMember = new ChallengeMember("owner", challenge, user);

        challengeMemberRepository.save(challengeMember);
    }
}
