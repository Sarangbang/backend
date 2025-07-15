package sarangbang.site.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponseDTO {

    // 업로드 성공 여부
    @Builder.Default
    private boolean success = true;

    // 업로드된 이미지에 접근할 수 있는 URL
    private String imageUrl;

    // 사용자에게 보여줄 메시지
    private String message;

    // 에러 정보 (프론트엔드에서 분기 처리용)
    private String errorCode;

    // 성공 응답 생성
    public static ImageUploadResponseDTO success(String imageUrl) {
        return ImageUploadResponseDTO.builder()
                .success(true)
                .imageUrl(imageUrl)
                .message("이미지가 성공적으로 업로드되었습니다.")
                .build();
    }

    // 실패 응답 생성
    public static ImageUploadResponseDTO failure(String errorCode, String message) {
        return ImageUploadResponseDTO.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}
