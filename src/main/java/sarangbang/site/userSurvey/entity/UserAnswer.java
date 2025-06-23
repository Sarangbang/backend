package sarangbang.site.userSurvey.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.global.entity.BaseEntity;

@Entity
@Table(name = "user_answer")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_answer_id")
    private Long userAnswerId;

    @Column(name = "selected_answer", nullable = false)
    private Integer selectedAnswer;


    // SurveySession과의 연관관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_session_id", nullable = false)
    private SurveySession surveySession;

    // 부분 생성자 (Service에서 사용)
    public UserAnswer(SurveySession surveySession, Integer selectedAnswer) {
        this.surveySession = surveySession;
        this.selectedAnswer = selectedAnswer;
    }
}