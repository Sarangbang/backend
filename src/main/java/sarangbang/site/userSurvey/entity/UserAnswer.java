package sarangbang.site.userSurvey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.global.entity.BaseEntity;

@Entity
@Table(name = "user_answer")
@Getter
@NoArgsConstructor
public class UserAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_answer_id")
    private Long userAnswerId;

    @Column(name = "selected_answer", nullable = false)
    private Integer selectedAnswer;

    // SurveyQuestion과의 연관관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_question_id")
    private SurveyQuestion surveyQuestion;

    // SurveySession과의 연관관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_session_id", nullable = false)
    private SurveySession surveySession;

    public UserAnswer(SurveySession surveySession, SurveyQuestion surveyQuestion,
                     Integer selectedAnswer) {
        this.surveySession = surveySession;
        this.surveyQuestion = surveyQuestion;
        this.selectedAnswer = selectedAnswer;
    }
}