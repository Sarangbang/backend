package sarangbang.site.challengeapplication.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.global.entity.BaseEntity;
import sarangbang.site.user.entity.User;

@Entity
@Table(name = "challenge_applications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChallengeApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String introduction;
    private String reason;
    private String commitment;
    private String status;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public ChallengeApplication(String introduction, String reason, String commitment, String status, String comment, User user, Challenge challenge) {
        this.introduction = introduction;
        this.reason = reason;
        this.commitment = commitment;
        this.status = status;
        this.comment = comment;
        this.user = user;
        this.challenge = challenge;
    }

    public void updateAppStatus(String status) {
        this.status = status;
    }

    public void updateAppComment(String comment) {
        this.comment = comment;
    }
}
