package sarangbang.site.userSurvey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sarangbang.site.userSurvey.entity.SurveySession;

import java.util.List;

public interface SurveySessionRepository extends JpaRepository<SurveySession, Long> {
    
    // 특정 사용자의 모든 설문 세션 조회 (최신순)
    @Query("SELECT s FROM SurveySession s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    List<SurveySession> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
    
    // 특정 사용자의 가장 최근 설문 세션 조회
    @Query("SELECT s FROM SurveySession s WHERE s.user.id = :userId ORDER BY s.createdAt DESC LIMIT 1")
    SurveySession findLatestByUserId(@Param("userId") String userId);
}