package sarangbang.site.challengecategory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.challengecategory.dto.ChallengeCategoryDTO;
import sarangbang.site.challengecategory.entity.ChallengeCategory;
import sarangbang.site.challengecategory.repository.ChallengeCategoryRepository;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChallengeCategoryService {

    private final ChallengeCategoryRepository challengeCategoryRepository;

    @PostConstruct
    @Transactional
    public void initCategories() {
        long count = challengeCategoryRepository.count();

        if (count == 0) {
            insertDefaultCategories();
        } else {
            log.info("카테고리 {}개가 이미 존재합니다.", count);
        }
    }

    /**
     * 기본 카테고리 데이터 삽입
     */
    private void insertDefaultCategories() {
        List<ChallengeCategory> defaultCategories = Arrays.asList(
            new ChallengeCategory("기상/루틴", "/images/charactors/default_wakeup.png", "/images/categories/sun.png"),
            new ChallengeCategory("학습/도서", "/images/charactors/default_study.png", "/images/categories/book.png"),
            new ChallengeCategory("운동/건강", "/images/charactors/default_health.png","/images/categories/runningshoes.png"),
            new ChallengeCategory("생활/정리", "/images/charactors/default_life.png", "/images/categories/box.png"),
            new ChallengeCategory("마음/감정", "/images/charactors/default_mind.png", "/images/categories/smile.png"),
            new ChallengeCategory("취미/자기계발", "/images/charactors/default_hobby.png", "/images/categories/pallete.png"),
            new ChallengeCategory("관계/소통", "/images/charactors/default_communication.png", "/images/categories/chat.png"),
            new ChallengeCategory("재테크/자산", "/images/charactors/default_money.png", "/images/categories/money.png"),
            new ChallengeCategory("그 외", "/images/charactors/default_general.png", "/images/categories/question.png")
        );
        
        challengeCategoryRepository.saveAll(defaultCategories);
    }

    /**
     * 모든 카테고리 조회 (이름순 정렬)
     */
    public List<ChallengeCategoryDTO> getAllCategories() {
        List<ChallengeCategory> categories = challengeCategoryRepository.findAll();
        return categories.stream()
                .map(ChallengeCategoryDTO::fromEntity)
                .toList();
    }

    /**
     * ID로 개별 카테고리 조회
     */
    public ChallengeCategoryDTO getCategoryById(Long categoryId) {
        ChallengeCategory category = challengeCategoryRepository.findById(categoryId).orElse(null);
        return category != null ? ChallengeCategoryDTO.fromEntity(category) : null;
    }
}