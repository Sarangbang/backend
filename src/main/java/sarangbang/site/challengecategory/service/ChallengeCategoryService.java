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
            new ChallengeCategory(null, "기상/루틴"),
            new ChallengeCategory(null, "학습/도서"),
            new ChallengeCategory(null, "생활/정리"),
            new ChallengeCategory(null, "마음/감정"),
            new ChallengeCategory(null, "취미/자기계발"),
            new ChallengeCategory(null, "관계/소통"),
            new ChallengeCategory(null, "재테크/자산"),
            new ChallengeCategory(null, "그 외")
        );
        
        challengeCategoryRepository.saveAll(defaultCategories);
    }

    /**
     * 모든 카테고리 조회 (이름순 정렬)
     */
    public List<ChallengeCategoryDTO> getAllCategories() {
        List<ChallengeCategory> categories = challengeCategoryRepository.findAllByOrderByCategoryName();
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