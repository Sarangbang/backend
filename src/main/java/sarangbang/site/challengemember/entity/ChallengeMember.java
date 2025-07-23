package sarangbang.site.challengemember.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.user.entity.User;

@Entity
@Table(name = "challenge_members")
@NoArgsConstructor
@Getter
public class ChallengeMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeMemberId;
    
    private String role;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public ChallengeMember(String role, Challenge challenge, User user) {
        this.role = role;
        this.challenge = challenge;
        this.user = user;
    }
}