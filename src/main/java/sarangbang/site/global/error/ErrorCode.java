package sarangbang.site.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    MISSING_REQUEST_PARAMETER(400, "필수 요청 파라미터가 누락되었습니다."),
    DIARY_NOT_FOUND(404, "해당 일기 목록이 존재하지 않습니다."),

    INTERNAL_SERVER_ERROR(500, "서버에 오류가 발생했습니다.");

    private final int status;
    private final String message;
}
