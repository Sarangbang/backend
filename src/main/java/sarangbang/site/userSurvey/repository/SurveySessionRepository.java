package sarangbang.site.userSurvey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sarangbang.site.userSurvey.entity.SurveySession;

import java.util.List;

public interface SurveySessionRepository extends JpaRepository<SurveySession, Long> {
}