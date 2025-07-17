package sarangbang.site.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sarangbang.site.region.entity.Region;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengePopularityResponseDTO {

    @Schema(description = "챌린지명", example = "JPA 정복 스터디")
    private String challengeTitle;
    @Schema(description = "챌린지 지역", example = "서울특별시")
    private Region region;
    @Schema(description = "챌린지 대표 이미지", example = "https://example.com/images/jpa_study.jpg")
    private String image;
    @Schema(description = "총 인원", example = "10")
    private int maxParticipants;
    @Schema(description = "현재 참여 인원", example = "5")
    private long currentParticipants;

}
