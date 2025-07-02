package sarangbang.site.challengeverification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challengeverification.entity.ChallengeVerification;
import sarangbang.site.user.entity.User;

import java.time.LocalDateTime;

public interface ChallengeVerificationRepository extends JpaRepository<ChallengeVerification, Long> {
    
    boolean existsByChallengeAndUserAndCreatedAtBetween(
            Challenge challenge, User user, LocalDateTime start, LocalDateTime end
    );
}
