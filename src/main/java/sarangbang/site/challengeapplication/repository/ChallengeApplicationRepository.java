package sarangbang.site.challengeapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.challengeapplication.enums.ChallengeApplyStatus;
import sarangbang.site.user.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeApplicationRepository extends JpaRepository<ChallengeApplication, Long> {
    ChallengeApplication findChallengeApplicationById(Long id);
    boolean existsByUserAndChallenge(User user, Challenge challenge);
    
    // 사용자의 특정 챌린지 신청 정보 조회 - 신청 상태 확인용
    Optional<ChallengeApplication> findByUserAndChallenge(User user, Challenge challenge);
    
    // 거절되지 않은 신청서 존재 여부 확인 (PENDING, APPROVED만 중복으로 처리)
    boolean existsByUserAndChallengeAndChallengeApplyStatusNot(User user, Challenge challenge, ChallengeApplyStatus status);

    @Query("SELECT ca FROM ChallengeApplication ca " +
            "JOIN FETCH ca.user " +
            "LEFT JOIN FETCH ca.user.region " +
            "WHERE ca.challenge.id = :challengeId " +
            "AND ca.challengeApplyStatus = :status " +
            "ORDER BY ca.createdAt DESC")
    List<ChallengeApplication> findByChallengeIdAndStatusWithUserAndRegion(@Param("challengeId") Long challengeId, @Param("status") ChallengeApplyStatus status);
    
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
