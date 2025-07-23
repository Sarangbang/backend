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
    private LocalDate startDate;
    private LocalDate endDate;
    private Long categoryId;
    private String categoryName;

    public ChallengeResponseDto(Challenge challenge, int currentParticipants, String imageUrl) {
        this.id = challenge.getId();
        this.title = challenge.getTitle();
        this.location = challenge.getRegion().getFullAddress();
        this.image = imageUrl;
        this.participants = challenge.getParticipants();
        this.currentParticipants = currentParticipants;
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.categoryId = challenge.getChallengeCategory().getCategoryId();
        this.categoryName = challenge.getChallengeCategory().getCategoryName();
    }

}
