package sarangbang.site.challengeapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sarangbang.site.challengeapplication.enums.Status;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeJoinDTO {

    @NotBlank(message = "자기소개를 입력해주세요.")
    @Size(max = 500, message = "자기소개는 500자 미만으로 작성해주세요.")
    private String introduction;

    @NotBlank(message = "신청사유를 입력해주세요.")
    @Size(max = 500, message = "신청사유는 500자 미만으로 작성해주세요.")
    private String reason;

    @NotBlank(message = "다짐을 입력해주세요.")
    @Size(max = 500, message = "다짐은 500자 미만으로 작성해주세요.")
    private String commitment;

    private Status status;

    private String comment;

    private Long challengeId;
}
