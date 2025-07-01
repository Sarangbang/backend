package sarangbang.site.challenge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.challengecategory.entity.ChallengeCategory;
import sarangbang.site.global.entity.BaseEntity;
import sarangbang.site.region.entity.Region;

import java.time.LocalDate;

@Entity
@Table(name = "Challenges")
@NoArgsConstructor
@Getter
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    @JoinColumn(name = "category_id")
    private ChallengeCategory challengeCategory;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    public Challenge(String title, String image, int participants, ChallengeCategory challengeCategory, Region region) {
        this.title = title;
        this.image = image;
        this.participants = participants;
        this.challengeCategory = challengeCategory;
        this.region = region;
    }
  
    public Challenge(String title, String description, int participants, String method, LocalDate startDate, LocalDate endDate, String image, boolean status, ChallengeCategory challengeCategory, Region region) {
        this.title = title;
        this.description = description;
        this.participants = participants;
        this.method = method;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
        this.status = status;
        this.challengeCategory = challengeCategory;
        this.region = region;
    }

}
