package sarangbang.site.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 📤 이미지 업로드 응답 DTO
 * 
 * 🎯 용도: 
 * - 프론트엔드에 업로드 결과 전달
 * - 업로드된 이미지 URL 제공
 * - 에러 발생 시 상세 정보 전달
 * 
 * 📋 사용 예시:
 * {
 *   "success": true,
 *   "imageUrl": "/api/files/challenges/20250714_abc123_image.jpg",
 *   "message": "이미지가 성공적으로 업로드되었습니다."
 * }
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponseDTO {

    /**
     * ✅ 업로드 성공 여부
     * true: 성공, false: 실패
     */
    @Builder.Default
    private boolean success = true;

    /**
     * 🔗 업로드된 이미지에 접근할 수 있는 URL
     * 
     * 예시: "/api/files/profiles/20250714_abc123_profile.jpg"
     * 프론트엔드에서 <img src={imageUrl}> 형태로 사용
     */
    private String imageUrl;

    /**
     * 💬 사용자에게 보여줄 메시지
     * 
     * 성공: "이미지가 성공적으로 업로드되었습니다."
     * 실패: "파일 크기가 너무 큽니다. (최대 5MB)"
     */
    private String message;

    /**
     * ❌ 에러 정보 (실패 시에만 포함)
     * 
     * 에러 코드, 상세 정보 등
     * 프론트엔드에서 에러 처리에 사용
     */
    private String errorCode;

    /**
     * ✅ 성공 응답 생성 헬퍼 메서드
     * 
     * @param imageUrl 업로드된 이미지 URL
     * @return 성공 응답 객체
     */
    public static ImageUploadResponseDTO success(String imageUrl) {
        return ImageUploadResponseDTO.builder()
                .success(true)
                .imageUrl(imageUrl)
                .message("이미지가 성공적으로 업로드되었습니다.")
                .build();
    }

    /**
     * ❌ 실패 응답 생성 헬퍼 메서드
     * 
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return 실패 응답 객체
     */
    public static ImageUploadResponseDTO failure(String errorCode, String errorMessage) {
        return ImageUploadResponseDTO.builder()
                .success(false)
                .message(errorMessage)
                .errorCode(errorCode)
                .build();
    }
}
