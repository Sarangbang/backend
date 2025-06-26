package sarangbang.site.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.challenge.entity.Challenge;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByChallengeCategory_CategoryId(Long categoryId);
    Challenge findChallengeById(Long challengeId);
}
