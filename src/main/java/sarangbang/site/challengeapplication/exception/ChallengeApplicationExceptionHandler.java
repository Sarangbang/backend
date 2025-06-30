package sarangbang.site.challengeapplication.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "sarangbang.site.challengeapplication")
public class ChallengeApplicationExceptionHandler {

    public record ErrorResponse(
            @Schema(description = "에러 상세 메시지", example = "이미 신청한 챌린지입니다.")
            String message
    ) {}

    // ChallengeAlreadyAppliedException이 발생하면 이 메서드가 실행됨
    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateApplicationException(DuplicateApplicationException e) {
        log.error("!! Challenge Already Applied: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        ResponseEntity<ErrorResponse> response = new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        return response; // 409 Conflict 응답
    }
}