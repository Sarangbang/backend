package sarangbang.site.challenge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sarangbang.site.challengecategory.entity.ChallengeCategory;
import sarangbang.site.global.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "Challenges")
@NoArgsConstructor
@Getter
@Setter
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String location;
    private String title;
    @Column(length = 500)
    private String description;
    private int participants;
    @Column(length = 500)
    private String method;
    private LocalDate startDate;
    private LocalDate endDate;
    private String image;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private ChallengeCategory challengeCategory;

    public Challenge(String location, String title, String description, int participants, String method, LocalDate startDate, LocalDate endDate, String image, boolean status, ChallengeCategory challengeCategory) {
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
