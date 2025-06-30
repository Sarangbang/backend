package sarangbang.site.region.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "sarangbang.site.region.controller")
public class RegionExceptionHandler {

    public record ErrorResponse(String message) {

    }

    @ExceptionHandler(RegionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRegionNotFoundException(RegionNotFoundException e) {
        log.warn("!! 지역을 찾을 수 없습니다: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "존재하지 않는 지역 요청입니다."
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
