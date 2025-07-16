package sarangbang.site.challengeverification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sarangbang.site.challengeverification.enums.ChallengeVerificationStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "챌린지 인증 응답 DTO")
public class ChallengeVerificationResponseDTO {
    
    @Schema(description = "챌린지 ID", example = "1")
    private Long challengeId;

    @Schema(description = "인증일", example = "2025-07-15 00:00:00.000000")
    private LocalDateTime verifiedAt;

    @Schema(description = "인증 이미지 URL", example = "https://example.com/image.jpg")
    private String imgUrl;

    @Schema(description = "인증 내용", example = "오늘 운동 완료했습니다!")
    private String content;

    @Schema(description = "인증 상태", example = "APPROVED")
    private ChallengeVerificationStatus status;
    
    @Schema(description = "인증한 사용자 ID", example = "user123")
    private String userId;
}