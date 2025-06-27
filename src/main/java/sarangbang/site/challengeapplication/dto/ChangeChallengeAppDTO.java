package sarangbang.site.challengeapplication.dto;

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
public class ChangeChallengeAppDTO {

    @NotBlank(message = "승인 여부를 선택해주세요.")
    private String status;
    @NotBlank(message = "승인/거부 코멘트를 작성해주세요.")
    @Size(max = 100, message = "승인/거부 코멘트는 100글자 미만으로 작성해주세요.")
    private String comment;
}
