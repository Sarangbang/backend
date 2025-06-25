package sarangbang.site.challenge.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sarangbang.site.challenge.dto.ChallengeDTO;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.repository.ChallengeRepository;
import sarangbang.site.challengecategory.entity.ChallengeCategory;
import sarangbang.site.challengecategory.repository.ChallengeCategoryRepository;
import sarangbang.site.challengemember.service.ChallengeMemberService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeCategoryRepository challengeCategoryRepository;
    private final ChallengeMemberService challengeMemberService;

    // 챌린지 등록
    @Transactional
    public ChallengeDTO saveChallenge(ChallengeDTO dto, String userId) {

        ChallengeCategory category = challengeCategoryRepository.findChallengeCategoryByCategoryId(dto.getCategoryId());
        log.debug("챌린지 카테고리 정보 : {}", category.getCategoryName());

        Challenge challenge = new Challenge(
                dto.getLocation(),
                dto.getTitle(),
                dto.getDescription(),
                dto.getParticipants(),
                dto.getMethod(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getImage(),
                dto.isStatus(),
                category
        );

        challengeRepository.save(challenge);
        challengeMemberService.saveChallengeOwner(userId, challenge.getId());

        ChallengeDTO challengeDTO = new ChallengeDTO(challenge.getLocation(),challenge.getTitle(), challenge.getDescription(),
                challenge.getParticipants(), challenge.getMethod(), challenge.getStartDate(), challenge.getEndDate(),
                challenge.getImage(), challenge.isStatus(), challenge.getChallengeCategory().getCategoryId());

        return challengeDTO;
    }
}
