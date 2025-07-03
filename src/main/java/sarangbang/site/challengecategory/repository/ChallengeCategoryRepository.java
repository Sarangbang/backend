package sarangbang.site.challengecategory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.challengecategory.entity.ChallengeCategory;

import java.util.List;

public interface ChallengeCategoryRepository extends JpaRepository<ChallengeCategory, Long> {

    ChallengeCategory findChallengeCategoryByCategoryId(Long categoryId);
}