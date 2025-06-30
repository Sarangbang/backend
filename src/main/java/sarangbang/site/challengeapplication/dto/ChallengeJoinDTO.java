package sarangbang.site.challengeapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import sarangbang.site.challengeapplication.enums.ChallengeApplyStatus;

@Data
@Schema(description = "챌린지 신청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeJoinDTO {

    @Schema(description = "자기소개", example = "안녕하세요.")
    @NotBlank(message = "자기소개를 입력해주세요.")
    @Size(max = 500, message = "자기소개는 500자 미만으로 작성해주세요.")
    private String introduction;

    @Schema(description = "신청사유", example = "테스트 하고 싶어요.")
    @NotBlank(message = "신청사유를 입력해주세요.")
    @Size(max = 500, message = "신청사유는 500자 미만으로 작성해주세요.")
    private String reason;

    @Schema(description = "다짐", example = "테스트 잘해요.")
    @NotBlank(message = "다짐을 입력해주세요.")
    @Size(max = 500, message = "다짐은 500자 미만으로 작성해주세요.")
    private String commitment;

    @Schema(description = "신청상태", example = "PENDING")
    private ChallengeApplyStatus challengeApplyStatus;

    @Schema(description = "챌린지", example = "1")
    private Long challengeId;
}
