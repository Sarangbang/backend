package sarangbang.site.global.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sarangbang.site.challenge.exception.ChallengeNotFoundException;
import sarangbang.site.global.error.ErrorCode;
import sarangbang.site.global.error.ErrorResponse;

@Slf4j
@RestControllerAdvice(basePackages = {
        "sarangbang.site.challenge",
        "sarangbang.site.challengeapplication",
        "sarangbang.site.challengecategory",
        "sarangbang.site.challengemember",
        "sarangbang.site.challengeverification"
})
@RequiredArgsConstructor
public class ChallengelExceptionHandler {

    @ExceptionHandler(ChallengeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChallengeNotFoundException(ChallengeNotFoundException e) {
        ErrorCode errorCode = ErrorCode.CHALLENGE_NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getStatus(), e.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));
    }
}
