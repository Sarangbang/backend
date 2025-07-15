package sarangbang.site.challenge.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.challenge.entity.Challenge;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Challenge findChallengeById(Long challengeId);

    List<Challenge> findChallengesByIdIn(Collection<Long> ids);
    Optional<Challenge> findByTitle(String title);

    Page<Challenge> findAll(Pageable pageable);

    Page<Challenge> findByChallengeCategory_CategoryId(Long categoryId, Pageable pageable);

    Page<Challenge> findAllByStatus(boolean status, Pageable pageable);

    Page<Challenge> findByChallengeCategory_CategoryIdAndStatus(Long challengeCategoryCategoryId, boolean status, Pageable pageable);
}
