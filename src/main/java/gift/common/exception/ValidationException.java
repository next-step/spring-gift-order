package gift.common.exception;

import gift.common.exception.core.CustomException;
import org.springframework.http.HttpStatus;

public class ValidationException extends CustomException {

    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getDefaultMessage() {
        return "유효성 검사에 실패했습니다.";
    }
}
