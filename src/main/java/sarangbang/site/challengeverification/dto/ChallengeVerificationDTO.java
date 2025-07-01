package sarangbang.site.challengeverification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sarangbang.site.challengeverification.enums.ChallengeVerificationStatus;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "챌린지 인증 DTO")
public class ChallengeVerificationDTO {
    private Long challengeId;

    @Schema(description = "인증 이미지 파일 (JPG, PNG만 가능, 최대 10MB)", type = "string", format = "binary")
    @NotNull(message = "이미지 파일은 필수입니다")
    private String imgUrl;

    @Schema(description = "인증 내용", example = "오늘 운동 완료했습니다!")
    @Size(max = 100, message = "내용은 100자 미만으로 작성해주세요.")
    private String content;

    @Schema(description = "인증 상태", example = "APPROVED")
    private ChallengeVerificationStatus status;
    
    @Schema(description = "인증한 사용자 ID", example = "user123")
    private String userId;

    public ChallengeVerificationDTO(Long challengeId, String imgUrl, String content, ChallengeVerificationStatus status, String userId) {
        this.challengeId = challengeId;
        this.imgUrl = imgUrl;
        this.content = content;
        this.status = status;
        this.userId = userId;
    }
}