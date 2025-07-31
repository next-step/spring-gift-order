package gift.common.exception.core;

import org.springframework.http.HttpStatus;

public abstract class CustomException extends RuntimeException {

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();

    public String getDefaultMessage() {
        return "알 수 없는 오류가 발생했습니다.";
    }

    @Override
    public String getMessage() {
        return super.getMessage() != null ? super.getMessage() : getDefaultMessage();
    }
}
