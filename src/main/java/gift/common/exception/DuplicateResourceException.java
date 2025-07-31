package gift.common.exception;

import gift.common.exception.core.CustomException;
import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends CustomException {

    public DuplicateResourceException() {
        super();
    }

    public DuplicateResourceException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getDefaultMessage() {
        return "이미 존재하는 리소스입니다.";
    }
}
