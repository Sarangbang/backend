package sarangbang.site.challengeapplication.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challengeapplication.enums.Status;
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

    @Enumerated(EnumType.STRING)
    private Status status;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public ChallengeApplication(String introduction, String reason, String commitment, Status status, String comment, User user, Challenge challenge) {
        this.introduction = introduction;
        this.reason = reason;
        this.commitment = commitment;
        this.status = status;
        this.comment = comment;
        this.user = user;
        this.challenge = challenge;
    }
}
