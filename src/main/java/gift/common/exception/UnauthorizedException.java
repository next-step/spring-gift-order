package gift.common.exception;

import gift.common.exception.core.CustomException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getDefaultMessage() {
        return "인증이 필요합니다.";
    }
}
