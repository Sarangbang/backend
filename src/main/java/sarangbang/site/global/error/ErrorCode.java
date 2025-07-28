package sarangbang.site.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    MISSING_REQUEST_PARAMETER(400, "필수 요청 파라미터가 누락되었습니다."),
    DIARY_NOT_FOUND(404, "해당 일기 목록이 존재하지 않습니다."),

    INTERNAL_SERVER_ERROR(500, "서버에 오류가 발생했습니다."),

    // 파일 관련 에러
    FILE_SIZE_EXCEEDED(400, "파일 크기가 허용된 범위를 초과했습니다."),
    INVALID_FILE_FORMAT(400, "지원하지 않는 파일 형식입니다."),
    INVALID_IMAGE_FILE(400, "유효하지 않은 이미지 파일입니다."),
    CORRUPTED_IMAGE_FILE(400, "이미지 파일이 손상되었습니다."),
    FILE_UPLOAD_FAILED(500, "파일 업로드에 실패했습니다.");

    private final int status;
    private final String message;
}
