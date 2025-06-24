package sarangbang.site.challengemember.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.user.entity.User;

@Entity
@Table(name = "ChallengeMembers")
@NoArgsConstructor
@Getter
public class ChallengeMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeMemberId;
    
    private String role;

    @ManyToOne
    @JoinColumn(name = "challengeId")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public ChallengeMember(String role, Challenge challenge, User user) {
        this.role = role;
        this.challenge = challenge;
        this.user = user;
    }
}