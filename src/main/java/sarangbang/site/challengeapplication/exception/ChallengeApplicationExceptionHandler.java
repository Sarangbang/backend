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
    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateApplicationException(DuplicateApplicationException e) {
        log.error("!! Challenge Already Applied: {}", e.getMessage());
        ErrorResponse response = new ErrorResponse("CHALLENGE_ALREADY_APPLIED", e.getMessage());
        ResponseEntity<ErrorResponse> responseEntity = new ResponseEntity<>(response, HttpStatus.CONFLICT);
        return responseEntity; // 409 Conflict 응답
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String code;
        private final String message;
    }
}
