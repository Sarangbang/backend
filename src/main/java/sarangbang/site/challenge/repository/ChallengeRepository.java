package sarangbang.site.challenge.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sarangbang.site.challenge.dto.ChallengePopularityResponseDTO;
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

    @Query("SELECT new sarangbang.site.challenge.dto.ChallengePopularityResponseDTO(" +
            "    c.title, " +
            "    c.region, " +
            "    c.image, " +
            "    c.participants, " +
            "    COUNT(cm.challengeMemberId)) " +
            "FROM Challenge c " +
            "LEFT JOIN ChallengeMember cm ON c.id = cm.challenge.id " +
            "GROUP BY c.id, c.title, c.region, c.image, c.participants " +
            "ORDER BY COUNT(cm.challengeMemberId) DESC")
    List<ChallengePopularityResponseDTO> findChallengesByPopularity();
}
