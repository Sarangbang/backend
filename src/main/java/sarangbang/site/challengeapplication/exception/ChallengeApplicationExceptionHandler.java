package sarangbang.site.challengeapplication.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "sarangbang.site.challengeapplication")
public class ChallengeApplicationExceptionHandler {

    // ChallengeAlreadyAppliedException이 발생하면 이 메서드가 실행됨
    @ExceptionHandler(ChallengeAlreadyAppliedException.class)
    public ResponseEntity<ErrorResponse> challengeAlreadyAppliedException(ChallengeAlreadyAppliedException e) {
        log.error("!! Challenge Already Applied: {}", e.getMessage());
        ErrorResponse response = new ErrorResponse("ALREADY_APPLIED", e.getMessage());
        ResponseEntity<ErrorResponse> responseEntity = new ResponseEntity<>(response, HttpStatus.CONFLICT);
        return responseEntity; // 409 Conflict 응답
    }

    // 위에서 처리하지 못한 모든 예외를 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        log.error("!! Unhandled Exception: ", e); // 스택 트레이스를 포함하여 로그 기록
        ErrorResponse response = new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error 응답
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String code;
        private final String message;
    }
}
