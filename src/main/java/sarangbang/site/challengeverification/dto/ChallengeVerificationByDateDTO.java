package sarangbang.site.challengeverification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "특정 챌린지 날짜별 인증 조회 DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeVerificationByDateDTO {

    @Schema(description = "챌린지 인증 ID", example = "1")
    private Long id;

    @Schema(description = "인증 이미지 경로", example = "https://example.com/images/challenge_image.jpg")
    private String imgUrl;

    @Schema(description = "챌린지 인증 여부", example = "APPROVED")
    private String status;

    @Schema(description = "챌린지 멤버 닉네임", example = "사용자_닉네임")
    private String nickname;
}
