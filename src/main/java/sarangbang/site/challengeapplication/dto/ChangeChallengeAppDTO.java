package sarangbang.site.challengeapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "신청서 수락/거부용 DTO")
public class ChangeChallengeAppDTO {

    @NotBlank(message = "승인 여부를 선택해주세요.")
    @Schema(description = "상태", example = "Approve")
    private String status;

    @NotBlank(message = "승인/거부 코멘트를 작성해주세요.")
    @Size(max = 100, message = "승인/거부 코멘트는 100글자 미만으로 작성해주세요.")
    @Schema(description = "코멘트", example = "승인합니다.")
    private String comment;
}
