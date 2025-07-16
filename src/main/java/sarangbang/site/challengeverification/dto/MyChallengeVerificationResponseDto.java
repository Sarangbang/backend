package sarangbang.site.challengeverification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "내 챌린지 인증 내역 조회 응답 DTO")
public class MyChallengeVerificationResponseDto {

    @Schema(description = "인증 사진 URL", example = "https://sarangbang-bucket.s3.ap-northeast-2.amazonaws.com/images/example.jpg")
    private final String imgUrl;
    @Schema(description = "챌린지 제목", example = "매일 아침 7시 기상 챌린지")
    private final String title;
    @Schema(description = "인증 수행 날짜", example = "2025-07-16 09:00:00.007")
    private final LocalDateTime verifiedAt;

    public MyChallengeVerificationResponseDto(String imgUrl, String title, LocalDateTime verifiedAt) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.verifiedAt = verifiedAt;
    }

}
