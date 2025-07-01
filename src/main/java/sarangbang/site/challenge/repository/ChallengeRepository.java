package sarangbang.site.challenge.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.challenge.entity.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
//    List<Challenge> findByChallengeCategory_CategoryId(Long categoryId);
    Challenge findChallengeById(Long challengeId);

    Page<Challenge> findAll(Pageable pageable);

    Page<Challenge> findByChallengeCategory_CategoryId(Long categoryId, Pageable pageable);
}
