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

import java.util.ArrayList;
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

    @Transactional
    @PostConstruct
    public void initChallengeData() {
        if (challengeRepository.count() == 0) {
            List<ChallengeCategory> categories = challengeCategoryRepository.findAll();

            if (!categories.isEmpty()) {
                ChallengeCategory category1 = categories.get(0);
                ChallengeCategory category2 = categories.get(1);

                Challenge challenge1 = new Challenge(
                    "7시 기상 챌린지", "집", "morning.jpg", 10, category1
                );

                Challenge challenge2 = new Challenge(
                    "홈트 30분", "집", "workout.jpg", 5, category1
                );

                Challenge challenge3 = new Challenge(
                    "방 정리하기", "집", "cleaning.jpg", 20, category2
                );

                challengeRepository.saveAll(Arrays.asList(challenge1, challenge2, challenge3));
            }
        }
    }
    /**
     * 전체 챌린지 목록 조회
     */
    public List<ChallengeResponseDto> getAllChallenges() {
        List<Challenge> challenges = challengeRepository.findAll();
        List<ChallengeResponseDto> responseDtos = new ArrayList<>();

        for (Challenge challenge : challenges) {
            int currentParticipants = challengeMemberRepository.countByChallengeId(challenge.getId());
            responseDtos.add(new ChallengeResponseDto(challenge, currentParticipants));
        }
        
        return responseDtos;
    }

//     return challenges.stream()
    //                .map(challenge -> {
    //                    int currentParticipants = challengeMemberRepository.countByChallengeId(challenge.getId());
    //                    return new ChallengeResponseDto(challenge, currentParticipants);
    //                })
    //                .collect(Collectors.toList());
//}
    
    /**
     * 카테고리별 챌린지 목록 조회
     */
    public List<ChallengeResponseDto> getChallengesByCategoryId(Long categoryId) {
        List<Challenge> challenges = challengeRepository.findByChallengeCategory_CategoryId(categoryId);
        List<ChallengeResponseDto> responseDtos = new ArrayList<>();

        for (Challenge challenge : challenges) {
            int currentParticipants = challengeMemberRepository.countByChallengeId(challenge.getId());
            responseDtos.add(new ChallengeResponseDto(challenge, currentParticipants));
        }
        
        return responseDtos;
    }
}
