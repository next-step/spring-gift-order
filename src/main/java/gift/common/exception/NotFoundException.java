package gift.common.exception;

import gift.common.exception.core.CustomException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getDefaultMessage() {
        return "요청한 리소스를 찾을 수 없습니다.";
    }
}
