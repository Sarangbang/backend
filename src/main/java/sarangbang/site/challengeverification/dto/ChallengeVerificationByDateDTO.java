package sarangbang.site.challengeverification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import sarangbang.site.challengeverification.enums.ChallengeVerificationStatus;

import java.time.LocalDateTime;

@Schema(description = "특정 챌린지 날짜별 인증 조회 DTO")
@Data
@NoArgsConstructor
public class ChallengeVerificationByDateDTO {

    @Schema(description = "챌린지 인증 ID", example = "1")
    private Long verificationId;

    @Schema(description = "사용자 식별값 ID", example = "TEST-UUID")
    private String userId;

    @Schema(description = "인증 이미지 경로", example = "https://example.com/images/challenge_image.jpg")
    private String imgUrl;

    @Schema(description = "챌린지 인증 여부", example = "APPROVED")
    private ChallengeVerificationStatus status;

    @Schema(description = "챌린지 멤버 닉네임", example = "사용자_닉네임")
    private String nickname;

    @Schema(description = "챌린지 멤버 등급", example = "owner")
    private String role;

    @Schema(description = "인증 내용", example = "오늘도 운동 완료!")
    private String content;

    @Schema(description = "인증 시간", example = "2025-06-20T15:36:00")
    private LocalDateTime verifiedAt;


    public ChallengeVerificationByDateDTO(Long verificationId, String userId, String imgUrl, ChallengeVerificationStatus status, String nickname, String role, String content, LocalDateTime verifiedAt) {
        this.verificationId = verificationId;
        this.userId = userId;
        this.imgUrl = imgUrl;
        this.status = status;
        this.nickname = nickname;
        this.role = role;
        this.content = content;
        this.verifiedAt = verifiedAt;
    }

    public ChallengeVerificationByDateDTO(Long verificationId, String userId, String imgUrl, String status, String nickname, String role, String content, LocalDateTime verifiedAt) {
        this.verificationId = verificationId;
        this.userId = userId;
        this.imgUrl = imgUrl;
        this.status = ChallengeVerificationStatus.valueOf(status);
        this.nickname = nickname;
        this.role = role;
        this.content = content;
        this.verifiedAt = verifiedAt;
    }
}
