package sarangbang.site.challengeverification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "챌린지 인증 요청 DTO")
public class ChallengeVerificationRequestDTO {
    
    private Long challengeId;

    @Schema(description = "인증 이미지 파일 (JPG, PNG만 가능, 최대 10MB)", type = "string", format = "binary")
    @NotNull(message = "이미지 파일은 필수입니다")
    private MultipartFile imageFile;

    @Schema(description = "인증 내용", example = "오늘 운동 완료했습니다!")
    @Size(max = 300, message = "내용은 300자 미만으로 작성해주세요.")
    private String content;
}