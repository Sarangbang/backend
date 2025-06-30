package sarangbang.site.challengeverification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.challengeverification.entity.ChallengeVerification;

public interface
ChallengeVerificationRepository extends JpaRepository<ChallengeVerification, Long> {
}
