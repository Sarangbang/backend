package sarangbang.site.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sarangbang.site.challenge.entity.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

    Challenge findChallengeById(int challengeId);
}
