package sarangbang.site.challengeapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.challengeapplication.enums.ChallengeApplyStatus;
import sarangbang.site.file.service.FileStorageService;

import java.time.Duration;
import java.time.LocalDateTime;

@Schema(description = "챌린지 관리 참여 신청 DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeApplicationDTO {

    // 식별 정보
    @Schema(description = "신청 ID", example = "1")
    private Long id;
    
    @Schema(description = "신청자 ID", example = "user-123")
    private String userId;


    // 신청자 기본 정보 (목록에서 사용)
    @Schema(description = "신청자 닉네임", example = "김감자")
    private String userNickname;
    
    @Schema(description = "신청자 프로필 이미지", example = "https://example.com/profile.jpg")
    private String userProfileImageUrl;
    
    @Schema(description = "신청자 지역", example = "서울특별시 강남구")
    private String userRegion;


    // 신청 상세 정보
    @Schema(description = "자기소개", example = "안녕하세요 김감자입니다. 개발의 정석!")
    private String introduction;
    
    @Schema(description = "신청사유", example = "꾸준하게 하고 싶어서 지원했어요 하하하하하 진짜 간절합니다")
    private String reason;
    
    @Schema(description = "다짐", example = "정말 열심히 함께요!!!!!!!!!!!!아자아자")
    private String commitment;
    
    @Schema(description = "신청상태", example = "PENDING")
    private ChallengeApplyStatus challengeApplyStatus;
    
    @Schema(description = "방장 코멘트", example = "승인합니다!")
    private String comment;
    
    @Schema(description = "신청일", example = "2025-07-23T07:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static ChallengeApplicationDTO from(ChallengeApplication application, FileStorageService fileStorageService) {
        // 프로필 이미지 presigned URL 생성
        String profileImageUrl = null;
        if (application.getUser().getProfileImageUrl() != null) {
            profileImageUrl = fileStorageService.generatePresignedUrl(
                    application.getUser().getProfileImageUrl(),
                    Duration.ofMinutes(10)
            );
        }

        return new ChallengeApplicationDTO(
                application.getId(),
                application.getUser().getId(),
                application.getUser().getNickname(),
                profileImageUrl, // presigned URL로 변경
                application.getUser().getRegion() != null ?
                        application.getUser().getRegion().getFullAddress() : null,
                application.getIntroduction(),
                application.getReason(),
                application.getCommitment(),
                application.getChallengeApplyStatus(),
                application.getComment(),
                application.getCreatedAt()
        );
    }
}
