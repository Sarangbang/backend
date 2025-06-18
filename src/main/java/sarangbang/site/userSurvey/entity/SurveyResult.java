package sarangbang.site.userSurvey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.global.entity.BaseEntity;

@Entity
@Table(name = "survey_result")
@Getter
@NoArgsConstructor
public class SurveyResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_result_id")
    private Long surveyResultId;

    @Column(name = "personality_avg_score", nullable = false)
    private Double personalityAvgScore;

    @Column(name = "dating_avg_score", nullable = false)
    private Double datingAvgScore;

    @Column(name = "lifestyle_avg_score", nullable = false)
    private Double lifestyleAvgScore;

    @Column(name = "total_avg_score", nullable = false)
    private Double totalAvgScore;

    @Column(name = "personality_code", length = 1, nullable = false)
    private String personalityCode;

    @Column(name = "dating_code", length = 1, nullable = false)
    private String datingCode;

    @Column(name = "lifestyle_code", length = 1, nullable = false)
    private String lifestyleCode;

    // SurveySession과의 연관관계 (1:1)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_session_id", nullable = false)
    private SurveySession surveySession;

    // UserTypeCode와의 연관관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_code", referencedColumnName = "type_code")
    private UserTypeCode userTypeCode;

    public SurveyResult(SurveySession surveySession, Double personalityAvgScore, Double datingAvgScore,
                       Double lifestyleAvgScore, Double totalAvgScore, String personalityCode,
                       String datingCode, String lifestyleCode, UserTypeCode userTypeCode) {
        this.surveySession = surveySession;
        this.personalityAvgScore = personalityAvgScore;
        this.datingAvgScore = datingAvgScore;
        this.lifestyleAvgScore = lifestyleAvgScore;
        this.totalAvgScore = totalAvgScore;
        this.personalityCode = personalityCode;
        this.datingCode = datingCode;
        this.lifestyleCode = lifestyleCode;
        this.userTypeCode = userTypeCode;
    }
}