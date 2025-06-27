package sarangbang.site.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "sarangbang.site.auth")
public class UserExceptionHandler {

    // 클라이언트에게 반환할 에러 응답 DTO
    public record ErrorResponse(String message) {}

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("[User Domain Exception] Illegal Argument Exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    // IllegalStateException 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        log.warn("[User Domain Exception] IllegalStateException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    // UserNotFoundException 과 같은 커스텀 예외 처리
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("[User Domain Exception] UserNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    // user 패키지 내에서 처리되지 않은 나머지 예외들
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("[User Domain Unhandled Exception] Exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("사용자 관련 처리 중 서버 내부 오류가 발생했습니다."));
    }

    // 동시성 상황에서 발생하는 에러
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("DataIntegrityViolationException: 데이터베이스 제약 조건 위반", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("이미 사용 중인 이메일 또는 닉네임입니다."));
    }
}
