package sarangbang.site.userSurvey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.global.entity.BaseEntity;
import sarangbang.site.user.entity.User;

@Entity
@Table(name = "survey_session")
@Getter
@NoArgsConstructor
public class SurveySession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_session_id")
    private Long surveySessionId;

    // User와의 연관관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id", nullable = false)
    private User user;

    public SurveySession(User user) {
        this.user = user;
    }
}