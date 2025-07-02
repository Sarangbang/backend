package sarangbang.site.challengeapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.user.entity.User;

@Repository
public interface ChallengeApplicationRepository extends JpaRepository<ChallengeApplication, Long> {
    ChallengeApplication findChallengeApplicationById(Long id);
    boolean existsByUserAndChallenge(User user, Challenge challenge);
}
