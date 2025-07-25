package sarangbang.site.challengeapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.user.entity.User;

import java.util.List;

@Repository
public interface ChallengeApplicationRepository extends JpaRepository<ChallengeApplication, Long> {
    ChallengeApplication findChallengeApplicationById(Long id);
    boolean existsByUserAndChallenge(User user, Challenge challenge);
    
    @Query("SELECT ca FROM ChallengeApplication ca " +
           "JOIN FETCH ca.user " +
           "LEFT JOIN FETCH ca.user.region " +
           "WHERE ca.challenge.id = :challengeId " +
           "ORDER BY ca.createdAt DESC")
    List<ChallengeApplication> findByChallengeIdWithUserAndRegion(@Param("challengeId") Long challengeId);
    
    @Query("SELECT ca FROM ChallengeApplication ca " +
           "JOIN FETCH ca.user " +
           "LEFT JOIN FETCH ca.user.region " +
           "WHERE ca.id = :applicationId")
    ChallengeApplication findByIdWithUserAndRegion(@Param("applicationId") Long applicationId);
}
