package sarangbang.site.challengeverification.dto;

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
public class TodayVerificationStatusResponseDTO {

    @Schema(description = "챌린지 id", example = "1")
    private Long challengeId;
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
    @Schema(description = "챌린지 인증 여부", example = "true")
    private boolean verifyStatus;

    public TodayVerificationStatusResponseDTO(Challenge challege, int currentParticipants, boolean verifyStatus) {
        this.challengeId = challege.getId();
        this.title = challege.getTitle();
        this.location = challege.getLocation();
        this.image = challege.getImage();
        this.participants = challege.getParticipants();
        this.currentParticipants = currentParticipants;
        this.verifyStatus = verifyStatus;
    }

}
