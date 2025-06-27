package sarangbang.site.challengeapplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChangeChallengeAppDTO {

    @NotBlank(message = "승인 여부를 선택해주세요.")
    private String status;
    @NotBlank(message = "사유를 작성해주세요.")
    private String comment;
}
