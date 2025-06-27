package sarangbang.site.challengeapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;

@Repository
public interface ChallengeApplicationRepository extends JpaRepository<ChallengeApplication, Long> {
    ChallengeApplication findChallengeApplicationById(Long id);
}
