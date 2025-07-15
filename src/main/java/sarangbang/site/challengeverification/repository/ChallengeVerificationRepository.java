package sarangbang.site.challengeverification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challengeverification.dto.ChallengeVerificationByDateDTO;
import sarangbang.site.challengeverification.entity.ChallengeVerification;
import sarangbang.site.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChallengeVerificationRepository extends JpaRepository<ChallengeVerification, Long> {
    
    boolean existsByChallengeAndUserAndCreatedAtBetween(
            Challenge challenge, User user, LocalDateTime start, LocalDateTime end
    );

    /* 특정 챌린지 날짜별 인증 조회 */
    @Query("SELECT NEW sarangbang.site.challengeverification.dto.ChallengeVerificationByDateDTO(" +
            "    cv.id, " +
            "    cm.user.id, " +
            "    cv.imgUrl," +
            "    CASE WHEN cv.status IS NOT NULL THEN cv.status ELSE 'PENDING' END," +
            "    cm.user.nickname) " +
            "FROM ChallengeMember cm " +
            "LEFT JOIN ChallengeVerification cv ON cm.challenge = cv.challenge AND cm.user = cv.user " +
            "AND cv.verifiedAt BETWEEN :startDate AND :endDate " +
            "WHERE cm.challenge.id = :challengeId "
            )
    List<ChallengeVerificationByDateDTO> findByChallengeAndVerifiedAt(
            @Param("challengeId") Long challengeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    Optional<ChallengeVerification> findByChallenge_IdAndUser_IdAndVerifiedAtBetween(Long challengeId, String userId, LocalDateTime start, LocalDateTime end);
    List<ChallengeVerification> findChallengeVerificationsByUser_IdAndVerifiedAt(String userId, LocalDateTime today);

    String user(User user);
}
