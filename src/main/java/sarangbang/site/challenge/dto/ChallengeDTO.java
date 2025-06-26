package sarangbang.site.challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDTO {

    @NotBlank(message = "챌린지 지역을 입력해주세요.")
    private String location;

    @NotBlank(message = "챌린지 제목을 입력해주세요.")
    @Size(max = 50, message = "제목은 50자 미만입니다.")
    private String title;

    @Size(max = 500, message = "내용은 500글자 미만으로 작성해주세요.")
    private String description;

    @NotNull(message = "챌린지 참여 인원을 입력해주세요.")
    private int participants;

    @NotBlank(message = "챌린지 인증 방법을 입력해주세요.")
    @Size(max = 500, message = "내용은 500글자 미만으로 작성해주세요.")
    private String method;

    @NotBlank
    private LocalDate startDate;

    @NotBlank
    private LocalDate endDate;

    private String image;

    private boolean status;

    @NotNull(message = "챌린지 주제를 선택해주세요.")
    private Long categoryId;

}
