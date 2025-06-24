package sarangbang.site.challenge.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.challenge.dto.ChallengeResponseDto;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.repository.ChallengeRepository;
import sarangbang.site.challengecategory.entity.ChallengeCategory;
import sarangbang.site.challengecategory.repository.ChallengeCategoryRepository;
import sarangbang.site.challengemember.repository.ChallengeMemberRepository;
import sarangbang.site.challengemember.entity.ChallengeMember;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeCategoryRepository challengeCategoryRepository;
    private final ChallengeMemberRepository challengeMemberRepository;

    @PostConstruct
    public void initChallengeData() {
        if (challengeRepository.count() == 0) {
            List<ChallengeCategory> categories = challengeCategoryRepository.findAll();

            if (!categories.isEmpty()) {
                ChallengeCategory category1 = categories.get(0);
                ChallengeCategory category2 = categories.get(1);

                Challenge challenge1 = new Challenge();
                challenge1.setTitle("7시 기상 챌린지");
                challenge1.setLocation("집");
                challenge1.setImage("morning.jpg");
                challenge1.setParticipants(4);
                challenge1.setChallengeCategory(category1);

                Challenge challenge2 = new Challenge();
                challenge2.setTitle("홈트 30분");
                challenge2.setLocation("집");
                challenge2.setImage("workout.jpg");
                challenge2.setParticipants(2);
                challenge2.setChallengeCategory(category1);

                Challenge challenge3 = new Challenge();
                challenge3.setTitle("방 정리하기");
                challenge3.setLocation("집");
                challenge3.setImage("cleaning.jpg");
                challenge3.setParticipants(12);
                challenge3.setChallengeCategory(category2);

                challengeRepository.saveAll(Arrays.asList(challenge1, challenge2, challenge3));
            }
        }
    }
    /**
     * 전체 챌린지 목록 조회
     */
    public List<ChallengeResponseDto> getAllChallenges() {
        List<Challenge> challenges = challengeRepository.findAll();
        
        return challenges.stream()
                .map(challenge -> {
                    int currentParticipants = challengeMemberRepository.countByChallengeId(challenge.getId());
                    return new ChallengeResponseDto(challenge, currentParticipants);
                })
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 챌린지 목록 조회
     */
    public List<ChallengeResponseDto> getChallengesByCategoryId(Long categoryId) {
        List<Challenge> challenges = challengeRepository.findByChallengeCategory_CategoryId(categoryId);
        
        return challenges.stream()
                .map(challenge -> {
                    int currentParticipants = challengeMemberRepository.countByChallengeId(challenge.getId());
                    return new ChallengeResponseDto(challenge, currentParticipants);
                })
                .collect(Collectors.toList());
    }
}
