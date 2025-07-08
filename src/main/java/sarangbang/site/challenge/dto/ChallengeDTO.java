package sarangbang.site.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "신청서 등록용 DTO")
public class ChallengeDTO {

    @NotBlank(message = "챌린지 지역을 입력해주세요.")
    @Schema(description = "지역 ID", example = "183")
    private Long regionId;

    @NotBlank(message = "챌린지 제목을 입력해주세요.")
    @Size(max = 50, message = "제목은 50자 미만입니다.")
    @Schema(description = "챌린지 제목", example = "6시 기상 챌린지")
    private String title;

    @Size(max = 500, message = "내용은 500글자 미만으로 작성해주세요.")
    @Schema(description = "챌린지 소개", example = "6시 기상을 목표로 하는 챌린지입니다.")
    private String description;

    @NotNull(message = "챌린지 참여 인원을 입력해주세요.")
    @Schema(description = "참여 인원", example = "10")
    private int participants;

    @NotBlank(message = "챌린지 인증 방법을 입력해주세요.")
    @Size(max = 500, message = "내용은 500글자 미만으로 작성해주세요.")
    @Schema(description = "인증 방법", example = "오전 6시 전에 기상 후 침대 사진을 찍어 올려주시면 됩니다.")
    private String method;

    @NotBlank
    @Schema(description = "챌린지 시작일", example = "2025-07-15")
    private LocalDate startDate;

    @NotBlank
    @Schema(description = "챌린지 종료일", example = "2025-08-15")
    private LocalDate endDate;

    @Schema(description = "챌린지 대표 이미지", example = "test-challenge.png")
    private String image;

    @Schema(description = "챌린지 활성화 상태", example = "true")
    private boolean status;

    @NotNull(message = "챌린지 주제를 선택해주세요.")
    @Schema(description = "챌린지 카테고리 Id", example = "1")
    private Long categoryId;

}
