package sarangbang.site.challenge.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Challenges")
@NoArgsConstructor
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String location;
    private String topic;
    private String title;
    private String descirption;
    private int participants;
    private String method;
    private LocalDate startDate;
    private LocalDate endDate;
    private String image;
    private char status;
}
