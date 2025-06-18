package sarangbang.site.userSurvey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sarangbang.site.userSurvey.entity.UserAnswer;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    // 특정 설문 세션의 모든 답변 조회 (문항 번호순)
    @Query("SELECT ua FROM UserAnswer ua " +
           "JOIN ua.surveyQuestion sq " +
           "WHERE ua.surveySession.surveySessionId = :sessionId " +
           "ORDER BY sq.questionNo")
    List<UserAnswer> findBySurveySessionIdOrderByQuestionNo(@Param("sessionId") Long sessionId);

    // 특정 설문 세션의 답변 개수 확인 (9개인지 체크)
    @Query("SELECT COUNT(ua) FROM UserAnswer ua WHERE ua.surveySession.surveySessionId = :sessionId")
    int countBySurveySessionId(@Param("sessionId") Long sessionId);
}