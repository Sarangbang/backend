package sarangbang.site.challengeverification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.global.entity.BaseEntity;
import sarangbang.site.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_verifications")
@NoArgsConstructor
@Getter
public class ChallengeVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private String imgUrl;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private VerificationStatus status;

    @Column(nullable = false)
    private String rejectionReason;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ChallengeVerification(LocalDateTime verifiedAt, String imgUrl, String content, VerificationStatus status, String rejectionReason, Challenge challenge, User user) {
        this.verifiedAt = verifiedAt;
        this.imgUrl = imgUrl;
        this.content = content;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.challenge = challenge;
        this.user = user;
    }

    public enum VerificationStatus {
        APPROVED, REJECTED
    }
}

