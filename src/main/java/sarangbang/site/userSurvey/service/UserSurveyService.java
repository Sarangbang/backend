package sarangbang.site.userSurvey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.user.entity.User;
import sarangbang.site.userSurvey.dto.SurveyAnswersDto;
import sarangbang.site.userSurvey.entity.SurveySession;
import sarangbang.site.userSurvey.entity.UserAnswer;
import sarangbang.site.userSurvey.repository.SurveySessionRepository;
import sarangbang.site.userSurvey.repository.UserAnswerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSurveyService {

    private final SurveySessionRepository surveySessionRepository;
    private final UserAnswerRepository userAnswerRepository;

    /**
     * 설문조사 답변 저장
     */
    @Transactional
    public void saveSurveyAnswers(User user, SurveyAnswersDto answersDto) {
        
        // 1. 설문 세션 생성 및 저장
        SurveySession session = createSurveySession(user);
        
        // 2. 사용자 답변 저장 (9개)
        saveUserAnswers(session, answersDto.getAnswers());
    }

    /**
     * 설문 세션 생성
     */
    private SurveySession createSurveySession(User user) {
        SurveySession session = new SurveySession(user);
        return surveySessionRepository.save(session);
    }

    /**
     * 사용자 답변 저장 (9개)
     */
    private void saveUserAnswers(SurveySession session, List<Integer> answers) {
        // 답변 개수 검증
        if (answers.size() != 9) {
            throw new IllegalArgumentException("설문 답변은 정확히 9개여야 합니다.");
        }
        
        // 각 답변을 UserAnswer 엔티티로 저장
        for (int i = 0; i < 9; i++) {
            Integer answerValue = answers.get(i);
            
            // 답변 값 검증 (1~5 범위)
            if (answerValue < 1 || answerValue > 5) {
                throw new IllegalArgumentException("답변은 1~5 사이의 값이어야 합니다.");
            }
            
            // SurveyQuestion 없이 질문 번호와 답변만 저장
            UserAnswer userAnswer = new UserAnswer(session, null, answerValue);
            userAnswerRepository.save(userAnswer);
        }
    }
}