package sarangbang.site.challengeverification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challengeverification.entity.ChallengeVerification;
import sarangbang.site.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeVerificationRepository extends JpaRepository<ChallengeVerification, Long> {
    
    boolean existsByChallengeAndUserAndCreatedAtBetween(
            Challenge challenge, User user, LocalDateTime start, LocalDateTime end
    );

    /* 특정 챌린지 날짜별 인증 조회 */
    @Query("SELECT cv FROM ChallengeVerification cv " +
            "WHERE cv.challenge.id = :challengeId " +
            "AND cv.verifiedAt BETWEEN :startDate AND :endDate")
    List<ChallengeVerification> findByChallengeAndVerifiedAt(
            @Param("challengeId") Long challengeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
