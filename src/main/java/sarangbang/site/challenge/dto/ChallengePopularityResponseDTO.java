package sarangbang.site.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.region.entity.Region;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengePopularityResponseDTO {

    @Schema(description = "챌린지id", example = "1")
    private Long challengeId;
    @Schema(description = "챌린지명", example = "JPA 정복 스터디")
    private String challengeTitle;
    @Schema(description = "챌린지 지역", example = "서울특별시")
    private String region;
    @Schema(description = "챌린지 대표 이미지", example = "https://example.com/images/jpa_study.jpg")
    private String image;
    @Schema(description = "총 인원", example = "10")
    private int maxParticipants;
    @Schema(description = "현재 참여 인원", example = "5")
    private Long currentParticipants;
    @Schema(description = "챌린지 시작일", example = "2025-07-24")
    private LocalDate startDate;
    @Schema(description = "챌린지 종료일", example = "2025-07-30")
    private LocalDate endDate;
    @Schema(description = "카테고리 id", example = "1")
    private Long categoryId;
    @Schema(description = "카테고리 이름", example = "기상/루틴")
    private String categoryName;

    public void updateImageUrl(String imageUrl) {
        this.image = imageUrl;
    }

}
