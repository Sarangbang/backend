package sarangbang.site.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.challenge.entity.Challenge;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponseDto {
    
    private Long id;
    private String title;
    private String location;
    private String image;
    private int participants;
    private int currentParticipants;

    public ChallengeResponseDto(Challenge challenge, int currentParticipants) {
        this.id = challenge.getId();
        this.title = challenge.getTitle();
        this.location = challenge.getLocation();
        this.image = challenge.getImage();
        this.participants = challenge.getParticipants();
        this.currentParticipants = currentParticipants;
    }
}