package sarangbang.site.userSurvey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import sarangbang.site.global.entity.BaseEntity;
import sarangbang.site.userSurvey.entity.enums.Category;

import java.util.List;

@Entity
@Table(name = "survey_question")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_question_id")
    private Long surveyQuestionId;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "question_no", nullable = false)
    private Integer questionNo;

    // UserAnswer와의 연관관계 (1:N)
    @OneToMany(mappedBy = "surveyQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAnswer> userAnswers;
}