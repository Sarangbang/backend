package sarangbang.site.challengeapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ChallengeAlreadyAppliedException extends RuntimeException {

    public ChallengeAlreadyAppliedException(String message) {
        super(message);
    }
}
