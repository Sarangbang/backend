package sarangbang.site.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 이 예외가 발생하면 409 Conflict 상태 코드를 반환
public class NicknameAlreadyExistsException extends RuntimeException {
    public NicknameAlreadyExistsException(String message) {
        super(message);
    }
}