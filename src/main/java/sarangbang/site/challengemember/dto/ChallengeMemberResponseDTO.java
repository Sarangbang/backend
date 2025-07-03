package sarangbang.site.challengemember.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "챌린지 제목", example = "JPA 정복 스터디")
    private String title;
    @Schema(description = "챌린지 지역", example = "서울특별시")
    private String location;
    @Schema(description = "챌린지 대표 이미지 URL", example = "https://example.com/images/jpa_study.jpg")
    private String image;
    @Schema(description = "최대 참여 가능 인원", example = "10")
    private int participants;
    @Schema(description = "현재 참여 인원", example = "5")
    private int currentParticipants;
    @Schema(description = "챌린지에서의 역할", example = "owner")
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
