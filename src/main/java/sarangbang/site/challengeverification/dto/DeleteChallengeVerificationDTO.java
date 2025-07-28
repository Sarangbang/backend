package sarangbang.site.challengeverification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DeleteChallengeVerificationDTO {

    @NotBlank
    @Schema(description = "사용자 id", example = "TEST_USER")
    private String userId;
    @NotNull
    @Schema(description = "챌린지 id", example = "1")
    private Long challengeId;
    @NotBlank
    @Schema(description = "인증일", example = "2025-07-28")
    private LocalDate verifiedAt;
}
