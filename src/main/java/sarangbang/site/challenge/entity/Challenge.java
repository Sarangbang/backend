package sarangbang.site.challenge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sarangbang.site.challengecategory.entity.ChallengeCategory;

import java.time.LocalDate;

@Entity
@Table(name = "Challenges")
@NoArgsConstructor
@Getter
@Setter
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

}
