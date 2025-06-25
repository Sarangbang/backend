package sarangbang.site.challenge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.challengecategory.entity.ChallengeCategory;

import java.time.LocalDate;

@Entity
@Table(name = "Challenges")
@NoArgsConstructor
@Getter
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String location;
    private String title;
    private String description;
    private int participants;
    private String method;
    private LocalDate startDate;
    private LocalDate endDate;
    private String image;
    private char status;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private ChallengeCategory challengeCategory;



    public Challenge(String title, String location, String image, int participants, ChallengeCategory challengeCategory) {
        this.title = title;
        this.location = location;
        this.image = image;
        this.participants = participants;
        this.challengeCategory = challengeCategory;
    }

    public Challenge(String location, String title, String description, int participants, String method, LocalDate startDate, LocalDate endDate, String image, char status, ChallengeCategory challengeCategory) {
        this.location = location;
        this.title = title;
        this.description = description;
        this.participants = participants;
        this.method = method;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
        this.status = status;
        this.challengeCategory = challengeCategory;
    }
}
