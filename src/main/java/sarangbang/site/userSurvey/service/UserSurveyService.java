package sarangbang.site.userSurvey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.repository.UserRepository;
import sarangbang.site.userSurvey.dto.SurveyAnswersDto;
import sarangbang.site.userSurvey.entity.SurveySession;
import sarangbang.site.userSurvey.entity.UserAnswer;
import sarangbang.site.userSurvey.repository.SurveySessionRepository;
import sarangbang.site.userSurvey.repository.UserAnswerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserSurveyService {

    private static final int REQUIRED_ANSWER_COUNT = 9; // 설문 질문 개수
    private static final int MIN_ANSWER_VALUE = 1; // 답변 최소 점수
    private static final int MAX_ANSWER_VALUE = 5; // 답변 최대 점수
    
    // 질문 ID 매핑 (순서 → questionId)
    private static final String[] QUESTION_IDS = {
        "personality_1", "personality_2", "personality_3",
        "love_1", "love_2", "love_3", 
        "lifestyle_1", "lifestyle_2", "lifestyle_3"
    };

    private final UserRepository userRepository;
    private final SurveySessionRepository surveySessionRepository;
    private final UserAnswerRepository userAnswerRepository;

    /**
     * 설문조사 답변 저장
     */
    @Transactional
    public void saveSurveyAnswers(String userId, SurveyAnswersDto answersDto) {
        log.debug("설문 답변 저장 시작 - userId: {}", userId);

        // 1. 사용자 조회 (Service 계층에서 처리)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자 - userId: {}", userId);
                    return new RuntimeException("User가 존재하지 않습니다: " + userId);
                });
        log.debug("사용자 조회 완료 - userId: {}", userId);

        // 2. 설문 세션 생성 및 저장
        SurveySession session = createSurveySession(user);
        log.debug("설문 세션 생성 완료 - sessionId: {}", session.getSurveySessionId());

        // 3. 사용자 답변 저장 (9개)
        saveUserAnswers(session, answersDto.getAnswers());
        log.info("설문 답변 저장 완료 - userId: {}, sessionId: {}, 답변 개수: {}",
                userId, session.getSurveySessionId(), answersDto.getAnswers().size());
    }

    /**
     * 설문 세션 생성
     */
    private SurveySession createSurveySession(User user) {
        SurveySession session = new SurveySession(user);
        return surveySessionRepository.save(session);
    }

    /**
     * 사용자 답변 저장
     */
    private void saveUserAnswers(SurveySession session, List<Integer> answers) {
        log.debug("답변 저장 시작 - sessionId: {}, 답변 개수: {}", session.getSurveySessionId(), answers.size());

        // 답변 개수 검증
        if (answers.size() != REQUIRED_ANSWER_COUNT) {
            log.error("잘못된 답변 개수 - sessionId: {}, 답변 개수: {}, 예상: {}",
                    session.getSurveySessionId(), answers.size(), REQUIRED_ANSWER_COUNT);
            throw new IllegalArgumentException("설문 답변은 정확히 " + REQUIRED_ANSWER_COUNT + "개여야 합니다.");
        }

        // 각 답변을 UserAnswer 엔티티로 저장
        for (Integer answerValue : answers) {
            // 답변 값 검증
            if (answerValue < MIN_ANSWER_VALUE || answerValue > MAX_ANSWER_VALUE) {
                log.error("잘못된 답변 값 - sessionId: {}, 답변 값: {}",
                        session.getSurveySessionId(), answerValue);
                throw new IllegalArgumentException("답변은 " + MIN_ANSWER_VALUE + "~" + MAX_ANSWER_VALUE + " 사이의 값이어야 합니다.");
            }

            // SurveyQuestion 없이 답변만 저장
            UserAnswer userAnswer = new UserAnswer(session, null, answerValue);
            userAnswerRepository.save(userAnswer);
            log.trace("답변 저장 완료 - sessionId: {}, 답변 값: {}",
                    session.getSurveySessionId(), answerValue);
        }
        
        log.debug("모든 답변 저장 완료 - sessionId: {}", session.getSurveySessionId());
    }
}