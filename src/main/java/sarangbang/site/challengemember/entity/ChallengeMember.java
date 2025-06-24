package sarangbang.site.challengemember.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.user.entity.User;

@Entity
@Table(name = "ChallengeMembers")
@NoArgsConstructor
@Getter
@Setter
public class ChallengeMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int challengeMemberId;
    
    private String role;

    @ManyToOne
    @JoinColumn(name = "challengeId")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}