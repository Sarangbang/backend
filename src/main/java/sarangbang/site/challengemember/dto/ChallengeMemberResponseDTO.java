package sarangbang.site.challengemember.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sarangbang.site.challenge.entity.Challenge;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeMemberResponseDTO {

    private String title;
    private String location;
    private String image;
    private int participants;
    private int currentParticipants;
    private String role;

    public ChallengeMemberResponseDTO(Challenge challenge, int currentParticipants, String role) {
        this.title = challenge.getTitle();
        this.location = challenge.getLocation();
        this.image = challenge.getImage();
        this.participants = challenge.getParticipants();
        this.currentParticipants = currentParticipants;
        this.role = role;
    }
}
