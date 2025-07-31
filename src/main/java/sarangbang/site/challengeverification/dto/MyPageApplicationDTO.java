package sarangbang.site.challengeverification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.challengeapplication.enums.ChallengeApplyStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "마이페이지 챌린지 신청내역 응답 DTO")
public class MyPageApplicationDTO {

    // 1. 신청서 자체 정보
    @Schema(description = "신청서 ID", example = "1")
    private Long applicationId;

    @Schema(description = "자기소개", example = "안녕하세요 이몽룡입니다")
    private String introduction;

    @Schema(description = "신청사유", example = "꾸준하게 일어나고 싶어 신청하게 되었습니다")
    private String reason;

    @Schema(description = "다짐", example = "진짜 열심히 하겠습니다")
    private String commitment;

    @Schema(description = "신청 상태", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    private ChallengeApplyStatus challengeApplyStatus;

    @Schema(description = "방장 코멘트", example = "반갑습니다!! 열심히 해봅시다")
    private String comment;

    // 2. 챌린지 정보
    @Schema(description = "챌린지 ID", example = "10")
    private Long challengeId;

    @Schema(description = "챌린지 제목", example = "매일 운동하기")
    private String challengeTitle;

    @Schema(description = "챌린지 설명", example = "매일 30분 이상 운동하고 인증하는 챌린지입니다.")
    private String challengeDescription;

    @Schema(description = "현재 참여자 수", example = "15")
    private int currentParticipants;

    @Schema(description = "최대 참여자 수", example = "30")
    private int maxParticipants;

    @Schema(description = "챌린지 이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "챌린지 지역", example = "경기도 가평군")
    private String location;

    // 화면 표시용 조합 데이터
    @Schema(description = "화면 표시용 제목 (참여자 수 포함)", example = "매일 운동하기 [15/30]")
    private String challengeDisplayTitle;

}
